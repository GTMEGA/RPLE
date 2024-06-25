/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
 */

package com.falsepattern.rple.internal.common.colorizer;

import com.falsepattern.rple.api.common.color.RPLENamedColor;
import com.falsepattern.rple.api.common.colorizer.RPLEBlockColorRegistry;
import com.falsepattern.rple.api.common.colorizer.RPLEBlockColorizer;
import com.falsepattern.rple.internal.common.block.RPLEBlockInit;
import com.falsepattern.rple.internal.common.config.container.BlockColorConfig;
import com.falsepattern.rple.internal.common.config.container.BlockReference;
import cpw.mods.fml.common.registry.GameRegistry;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.val;
import net.minecraft.block.Block;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.IdentityHashMap;

import static com.falsepattern.rple.internal.RPLEDefaultValues.*;
import static com.falsepattern.rple.internal.common.colorizer.NullBlockColorizer.nullBlockColorizer;
import static com.falsepattern.rple.internal.common.config.ColorConfigLoader.loadCustomConfig;
import static com.falsepattern.rple.internal.common.config.ColorConfigLoader.saveGeneratedConfig;
import static com.falsepattern.rple.internal.common.event.EventPoster.postBlockColorRegistrationEvent;
import static com.falsepattern.rple.internal.common.util.LogHelper.createLogger;
import static com.falsepattern.rple.internal.common.world.RPLEWorldProvider.*;
import static lombok.AccessLevel.PRIVATE;

@Accessors(fluent = true, chain = false)
@NoArgsConstructor(access = PRIVATE)
public final class BlockColorManager implements RPLEBlockColorRegistry {
    private static final Logger LOG = createLogger("BlockColorManager");

    private static final BlockColorManager INSTANCE = new BlockColorManager();

    private BlockColorConfig config;

    private boolean registryLocked = false;

    public static BlockColorManager blockColorManager() {
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

        val colouredBlocks = new IdentityHashMap<RPLEBlockInit, ColoredBlockedReference>();

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
                colouredBlock.metaBrightnessColorsMap(blockMeta.get(), brightness);
            } else {
                colouredBlock.baseBrightnessColor(brightness);
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
                colouredBlock.metaTranslucencyColorsMap(blockMeta.get(), translucency);
            } else {
                colouredBlock.baseTranslucencyColor(translucency);
            }
        }

        colouredBlocks.values().forEach(ColoredBlockedReference::apply);

        val configHashCode = "0x" + String.format("%08x", config.hashCode()).toUpperCase();
        redRPLEWorldProvider().applyConfigHashCode(configHashCode);
        greenRPLEWorldProvider().applyConfigHashCode(configHashCode);
        blueRPLEWorldProvider().applyConfigHashCode(configHashCode);

        registryLocked = true;
    }

    @Override
    public @NotNull RPLEBlockColorizer colorizeBlock(@NotNull Block block) {
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
    public @NotNull RPLEBlockColorizer colorizeBlock(@NotNull Block block, int blockMeta) {
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
    public @NotNull RPLEBlockColorizer colorizeBlock(@NotNull String blockID) {
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

    private @Nullable RPLEBlockInit blockFromBlockReference(BlockReference blockReference) {
        if (blockReference == null)
            return null;

        val blockDomain = blockReference.domain();
        val blockName = blockReference.name();
        if (blockDomain == null || blockName == null)
            return null;

        val block = GameRegistry.findBlock(blockDomain, blockName);
        if (block instanceof RPLEBlockInit)
            return (RPLEBlockInit) block;
        return null;
    }

    private void applyBlockColors(BlockColorizer colorizer) {
        if (registryLocked) {
            LOG.error("Cannot to apply block colors after post init", new Throwable());
            return;
        }

        colorizer.paletteBrightness().ifPresent(this::addPaletteColor);
        colorizer.paletteTranslucency().ifPresent(this::addPaletteColor);
        colorizer.brightness().ifPresent(this::setBlockBrightness);
        colorizer.translucency().ifPresent(this::setBlockTranslucency);
    }

    private void setBlockBrightness(BlockColorizer.BlockColorReference blockColor) {
        if (!blockColor.isValid()) {
            LOG.error("Failed to apply invalid block brightness color", new IllegalArgumentException());
            return;
        }
        val block = blockColor.block();
        val color = blockColor.color();

        val oldColor = config.setBlockBrightness(block, color);
        if (oldColor.isPresent()) {
            LOG.warn("Re-Colorized block [{}] with old brightness [{}] to [{}]", block, oldColor.get(), color);
        } else {
            LOG.info("Colorized block [{}] with brightness [{}]", block, color);
        }
    }

    private void setBlockTranslucency(BlockColorizer.BlockColorReference blockColor) {
        if (!blockColor.isValid()) {
            LOG.error("Failed to apply invalid block translucency color", new IllegalArgumentException());
            return;
        }
        val block = blockColor.block();
        val color = blockColor.color();

        val oldColor = config.setBlockTranslucency(block, color);
        if (oldColor.isPresent()) {
            LOG.warn("Re-Colorized block [{}] with old translucency [{}] to [{}]", block, oldColor.get(), color);
        } else {
            LOG.info("Colorized block [{}] with translucency [{}]", block, color);
        }
    }

    private void addPaletteColor(RPLENamedColor color) {
        config.addPaletteColor(color);
    }
}
