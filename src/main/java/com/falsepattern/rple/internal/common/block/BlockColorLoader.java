/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.block;

import com.falsepattern.rple.api.block.RPLEBlockColorRegistry;
import com.falsepattern.rple.api.block.RPLEBlockColorizer;
import com.falsepattern.rple.api.color.RPLENamedColor;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.config.container.BlockColorConfig;
import com.falsepattern.rple.internal.config.container.BlockReference;
import com.falsepattern.rple.internal.mixin.interfaces.IColoredBlockMixin;
import cpw.mods.fml.common.registry.GameRegistry;
import lombok.NoArgsConstructor;
import lombok.val;
import net.minecraft.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.IdentityHashMap;

import static com.falsepattern.rple.internal.common.RPLEDefaultValues.*;
import static com.falsepattern.rple.internal.common.block.NullBlockColorizer.nullBlockColorizer;
import static com.falsepattern.rple.internal.config.ColorConfigHandler.loadCustomConfig;
import static com.falsepattern.rple.internal.config.ColorConfigHandler.saveGeneratedConfig;
import static com.falsepattern.rple.internal.event.EventPoster.postBlockColorRegistrationEvent;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BlockColorLoader implements RPLEBlockColorRegistry {
    private static final Logger LOG = LogManager.getLogger(Tags.MOD_NAME + "|" + "Block Color Loader");

    private static final BlockColorLoader INSTANCE = new BlockColorLoader();

    private BlockColorConfig config;

    private boolean registryLocked = false;

    public static BlockColorLoader blockColorLoader() {
        return INSTANCE;
    }

    public void registerBlockColors() {
        if (registryLocked)
            return;

        config = new BlockColorConfig();
        preloadDefaultColorPalette(config);

        registerDefaultBlockBrightnessColors(this);
        registerDefaultBlockTranslucencyColors(this);
        postBlockColorRegistrationEvent(this);

        try {
            saveGeneratedConfig(config);
        } catch (IOException e) {
            LOG.error("Failed to save generated config", e);
        }

        try {
            loadCustomConfig().ifPresent(customConfig -> config = customConfig);
        } catch (IOException e) {
            LOG.error("Failed to load custom config", e);
            config = new BlockColorConfig();
        }

        val colorPalette = config.palette();
        val blockBrightnessMap = config.brightness();
        val blockTranslucencyMap = config.translucency();

        val colouredBlocks = new IdentityHashMap<IColoredBlockMixin, ColoredBlockedReference>();

        for (val blockBrightness : blockBrightnessMap.entrySet()) {
            val blockReference = blockBrightness.getKey();
            if (!blockReference.isValid())
                continue;

            val block = blockFromBlockReference(blockReference);
            if (block == null)
                continue;

            val blockColorReference = blockBrightness.getValue();
            val brightness = blockColorReference.color(colorPalette);

            val colouredBlock = colouredBlocks.computeIfAbsent(block, ColoredBlockedReference::new);

            val blockMeta = blockReference.meta();
            if (blockMeta.isPresent()) {
                colouredBlock.metaBrightness(blockMeta.get(), brightness);
            } else {
                colouredBlock.brightness(brightness);
            }
        }

        for (val blockTranslucency : blockTranslucencyMap.entrySet()) {
            val blockReference = blockTranslucency.getKey();
            if (!blockReference.isValid())
                continue;

            val block = blockFromBlockReference(blockReference);
            if (block == null)
                continue;

            val blockColorReference = blockTranslucency.getValue();
            val translucency = blockColorReference.color(colorPalette);

            val colouredBlock = colouredBlocks.computeIfAbsent(block, ColoredBlockedReference::new);

            val blockMeta = blockReference.meta();
            if (blockMeta.isPresent()) {
                colouredBlock.metaTranslucency(blockMeta.get(), translucency);
            } else {
                colouredBlock.translucency(translucency);
            }
        }

        colouredBlocks.values().forEach(ColoredBlockedReference::apply);

        registryLocked = true;
    }

    @Override
    public RPLEBlockColorizer colorizeBlock(@NotNull Block block) {
        if (registryLocked) {
            LOG.error("Block cannot be colorized after post init", new Throwable());
            return nullBlockColorizer();
        }

        val blockID = blockIDFromBlock(block);
        if (blockID == null)
            return nullBlockColorizer();

        return new BlockColorizer(new BlockReference(blockID), this::applyBlockColors);
    }

    @Override
    public RPLEBlockColorizer colorizeBlock(@NotNull Block block, int blockMeta) {
        if (registryLocked) {
            LOG.error("Block cannot be colorized after post init", new Throwable());
            return nullBlockColorizer();
        }

        val blockID = blockIDFromBlock(block);
        if (blockID == null)
            return nullBlockColorizer();

        return new BlockColorizer(new BlockReference(blockID + ":" + blockMeta), this::applyBlockColors);
    }

    @Override
    public RPLEBlockColorizer colorizeBlock(@NotNull String blockID) {
        if (registryLocked) {
            LOG.error("Block cannot be colorized after post init", new Throwable());
            return nullBlockColorizer();
        }

        if (blockID == null) {
            LOG.error("Block ID can't be null", new Throwable());
            return nullBlockColorizer();
        }

        return new BlockColorizer(new BlockReference(blockID), this::applyBlockColors);
    }

    private @Nullable String blockIDFromBlock(@Nullable Block block) {
        if (block == null) {
            LOG.error("Block can't be null", new Throwable());
            return null;
        }

        val blockID = GameRegistry.findUniqueIdentifierFor(block);
        if (blockID == null) {
            LOG.error("Block not registered", new Throwable());
            return null;
        }

        val blockDomain = blockID.modId;
        if (blockDomain == null) {
            LOG.error("Block domain can't be null", new Throwable());
            return null;
        }

        val blockName = blockID.name;
        if (blockName == null) {
            LOG.error("Block name can't be null", new Throwable());
            return null;
        }

        return blockDomain + ":" + blockName;
    }

    private @Nullable IColoredBlockMixin blockFromBlockReference(BlockReference blockReference) {
        if (blockReference == null)
            return null;

        val blockDomain = blockReference.domain();
        val blockName = blockReference.name();
        if (blockDomain == null || blockName == null)
            return null;

        val block = GameRegistry.findBlock(blockDomain, blockName);
        if (block instanceof IColoredBlockMixin)
            return (IColoredBlockMixin) block;
        return null;
    }

    private void applyBlockColors(BlockColorizer colorizer) {
        if (registryLocked) {
            LOG.error("Failed to apply block colors after post init", new Throwable());
            return;
        }

        colorizer.brightness().ifPresent(this::setBlockBrightness);
        colorizer.translucency().ifPresent(this::setBlockTranslucency);
        colorizer.paletteBrightness().ifPresent(this::addPaletteColor);
        colorizer.paletteTranslucency().ifPresent(this::addPaletteColor);
    }

    private void setBlockBrightness(BlockColorizer.BlockColorReference blockColor) {
        if (!blockColor.isValid()) {
            LOG.error("Failed to apply invalid block color", new Throwable());
            return;
        }
        config.setBlockBrightness(blockColor.block(), blockColor.color());
    }

    private void setBlockTranslucency(BlockColorizer.BlockColorReference blockColor) {
        if (!blockColor.isValid()) {
            LOG.error("Failed to apply invalid block color", new Throwable());
            return;
        }
        config.setBlockTranslucency(blockColor.block(), blockColor.color());
    }

    private void addPaletteColor(RPLENamedColor color) {
        config.addPaletteColor(color);
    }
}
