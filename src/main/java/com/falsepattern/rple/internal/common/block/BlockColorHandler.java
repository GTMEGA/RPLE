/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.block;

import com.falsepattern.rple.api.block.BlockColorRegistry;
import com.falsepattern.rple.api.block.ColoredLightBlock;
import com.falsepattern.rple.api.block.ColoredTranslucentBlock;
import com.falsepattern.rple.api.block.RPLEBlockColorizer;
import com.falsepattern.rple.api.color.GreyscaleColor;
import com.falsepattern.rple.api.color.RPLEColor;
import com.falsepattern.rple.internal.config.container.BlockReference;
import com.falsepattern.rple.internal.config.container.ColorConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BlockColorHandler implements BlockColorRegistry {
    private static final BlockColorHandler INSTANCE = new BlockColorHandler();

    public static BlockColorHandler blockColorHandler() {
        return INSTANCE;
    }

    public RPLEColor blockColoredBrightness(IBlockAccess world,
                                            Block block,
                                            int blockMeta,
                                            int posX,
                                            int posY,
                                            int posZ) {
        if (block instanceof ColoredLightBlock) {
            val colouredLightBlock = (ColoredLightBlock) block;
            val color = colouredLightBlock.getColoredBrightness(world, blockMeta, posX, posY, posZ);
            if (color != null)
                return color;
        }

        // Lookup happens here!

        val vanillaLightValue = block.getLightValue(world, posX, posY, posZ);
        return GreyscaleColor.fromVanillaLightValue(vanillaLightValue);
    }

    public RPLEColor blockColoredTranslucency(IBlockAccess world,
                                              Block block,
                                              int blockMeta,
                                              int posX,
                                              int posY,
                                              int posZ) {
        if (block instanceof ColoredTranslucentBlock) {
            val colouredTranslucentBlock = (ColoredTranslucentBlock) block;
            val color = colouredTranslucentBlock.getColoredTranslucency(world, blockMeta, posX, posY, posZ);
            if (color != null)
                return color;
        }

        // Lookup happens here!

        val vanillaLightOpacity = block.getLightOpacity(world, posX, posY, posZ);
        return GreyscaleColor.fromVanillaLightOpacity(vanillaLightOpacity);
    }

    @Override
    public RPLEBlockColorizer colorizeBlock(Block block) {
        return new BlockColorizer(new BlockReference(block), this::applyBlockColors);
    }

    @Override
    public RPLEBlockColorizer colorizeBlock(Block block, int blockMeta) {
        return new BlockColorizer(new BlockReference(block, blockMeta), this::applyBlockColors);
    }

    @Override
    public RPLEBlockColorizer colorizeBlock(String blockID) {
        return new BlockColorizer(new BlockReference(blockID), this::applyBlockColors);
    }

    private void applyBlockColors(BlockColorizer colorizer) {
        val foo = new ColorConfig();

        val block = colorizer.block();

        foo.setBlockBrightness(block, colorizer.brightness().get());
        foo.setBlockTranslucency(block, colorizer.translucency().get());

        foo.addPaletteColor(colorizer.paletteBrightness().get());
        foo.addPaletteColor(colorizer.paletteTranslucency().get());
    }
}
