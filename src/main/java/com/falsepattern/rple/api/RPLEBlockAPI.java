/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api;

import com.falsepattern.rple.api.block.ColoredLightBlock;
import com.falsepattern.rple.api.block.ColoredTranslucentBlock;
import com.falsepattern.rple.api.color.RPLEColor;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.Nullable;

import static com.falsepattern.rple.api.RPLEColorAPI.errorColor;

@UtilityClass
public final class RPLEBlockAPI {
    public static RPLEColor getColoredBrightnessSafe(Block block) {
        val colouredLightBlock = colouredLightBlockFromBlock(block);
        if (colouredLightBlock == null)
            return errorColor();

        try {
            val color = colouredLightBlock.getColoredBrightness();
            if (color != null)
                return color;
        } catch (Exception ignored) {
        }

        return errorColor();
    }

    public static RPLEColor getColoredBrightnessSafe(Block block, int blockMeta) {
        val colouredLightBlock = colouredLightBlockFromBlock(block);
        if (colouredLightBlock == null)
            return errorColor();

        try {
            val color = colouredLightBlock.getColoredBrightness(blockMeta);
            if (color != null)
                return color;
        } catch (Exception ignored) {
        }

        return errorColor();
    }

    public static RPLEColor getColoredBrightnessSafe(IBlockAccess world, Block block, int blockMeta, int posX, int posY, int posZ) {
        val colouredLightBlock = colouredLightBlockFromBlock(block);
        if (colouredLightBlock == null)
            return errorColor();

        try {
            val color = colouredLightBlock.getColoredBrightness(world, blockMeta, posX, posY, posZ);
            if (color != null)
                return color;
        } catch (Exception ignored) {
        }

        return errorColor();
    }

    public static RPLEColor getColoredTranslucencySafe(Block block) {
        val colouredLightBlock = colouredTranslucentBlockFromBlock(block);
        if (colouredLightBlock == null)
            return errorColor();

        try {
            val color = colouredLightBlock.getColoredTranslucency();
            if (color != null)
                return color;
        } catch (Exception ignored) {
        }

        return errorColor();
    }

    public static RPLEColor getColoredTranslucencySafe(Block block, int blockMeta) {
        val colouredLightBlock = colouredTranslucentBlockFromBlock(block);
        if (colouredLightBlock == null)
            return errorColor();

        try {
            val color = colouredLightBlock.getColoredTranslucency(blockMeta);
            if (color != null)
                return color;
        } catch (Exception ignored) {
        }

        return errorColor();
    }

    public static RPLEColor getColoredTranslucencySafe(IBlockAccess world, Block block, int blockMeta, int posX, int posY, int posZ) {
        val colouredLightBlock = colouredTranslucentBlockFromBlock(block);
        if (colouredLightBlock == null)
            return errorColor();

        try {
            val color = colouredLightBlock.getColoredTranslucency(world, blockMeta, posX, posY, posZ);
            if (color != null)
                return color;
        } catch (Exception ignored) {
        }

        return errorColor();
    }

    private static @Nullable ColoredLightBlock colouredLightBlockFromBlock(Block block) {
        if (block == null)
            return null;
        if (block instanceof ColoredLightBlock)
            return (ColoredLightBlock) block;

        return null;
    }

    private static @Nullable ColoredTranslucentBlock colouredTranslucentBlockFromBlock(Block block) {
        if (block == null)
            return null;
        if (block instanceof ColoredTranslucentBlock)
            return (ColoredTranslucentBlock) block;

        return null;
    }
}
