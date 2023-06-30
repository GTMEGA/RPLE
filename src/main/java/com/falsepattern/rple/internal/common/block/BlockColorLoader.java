/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.block;

import com.falsepattern.rple.api.block.BlockColorRegistry;
import com.falsepattern.rple.api.block.RPLEBlockColorizer;
import com.falsepattern.rple.api.color.RPLENamedColor;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.config.container.BlockColorConfig;
import com.falsepattern.rple.internal.config.container.BlockReference;
import lombok.NoArgsConstructor;
import lombok.val;
import net.minecraft.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.falsepattern.rple.internal.common.RPLEDefaultValues.preloadDefaultColorPalette;
import static com.falsepattern.rple.internal.common.RPLEDefaultValues.registerDefaultBlockColors;
import static com.falsepattern.rple.internal.event.EventPoster.postBlockColorRegistrationEvent;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BlockColorLoader implements BlockColorRegistry {
    private static final Logger LOG = LogManager.getLogger(Tags.MODNAME + "|" + "Block Color Loader");

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

        registerDefaultBlockColors(this);
        postBlockColorRegistrationEvent(this);

        // Save the config to disk
        // Try to find the user config
        // If found, try load if
        // If load is successful, use it
        // If load fails, load an empty config
        // Iterate over the config and apply it to each block

        registryLocked = true;
    }

    @Override
    public RPLEBlockColorizer colorizeBlock(Block block) {
        if (registryLocked)
            LOG.error("Block cannot be colorized after post init", new Throwable());
        return new BlockColorizer(new BlockReference(block), this::applyBlockColors);
    }

    @Override
    public RPLEBlockColorizer colorizeBlock(Block block, int blockMeta) {
        if (registryLocked)
            LOG.error("Block cannot be colorized after post init", new Throwable());
        return new BlockColorizer(new BlockReference(block, blockMeta), this::applyBlockColors);
    }

    @Override
    public RPLEBlockColorizer colorizeBlock(String blockID) {
        if (registryLocked)
            LOG.error("Block cannot be colorized after post init", new Throwable());
        return new BlockColorizer(new BlockReference(blockID), this::applyBlockColors);
    }

    private void applyBlockColors(BlockColorizer colorizer) {
        if (registryLocked) {
            LOG.error("Failed to apply block colors after post init", new Throwable());
            return;
        }

        val block = colorizer.block();
        if (!block.isValid()) {
            LOG.error("Failed to apply color to invalid block", new Throwable());
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
