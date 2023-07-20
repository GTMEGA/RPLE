/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api;

import com.falsepattern.rple.api.block.RPLEBlockBrightness;
import com.falsepattern.rple.api.block.RPLEBlockTranslucency;
import com.falsepattern.rple.api.color.RPLEColor;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.falsepattern.rple.api.RPLEColorAPI.errorColor;

@SuppressWarnings("unused")
public final class RPLEBlockAPI {
    private RPLEBlockAPI() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static @NotNull RPLEColor getBlockColoredBrightness(@NotNull Block block) {
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

    public static @NotNull RPLEColor getBlockColoredBrightness(@NotNull Block block, int blockMeta) {
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

    public static @NotNull RPLEColor getBlockColoredBrightness(@NotNull IBlockAccess world,
                                                               @NotNull Block block,
                                                               int blockMeta,
                                                               int posX,
                                                               int posY,
                                                               int posZ) {
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

    public static @NotNull RPLEColor getBlockColoredTranslucency(@NotNull Block block) {
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

    public static @NotNull RPLEColor getBlockColoredTranslucency(@NotNull Block block, int blockMeta) {
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

    public static @NotNull RPLEColor getBlockColoredTranslucency(@NotNull IBlockAccess world,
                                                                 @NotNull Block block,
                                                                 int blockMeta,
                                                                 int posX,
                                                                 int posY,
                                                                 int posZ) {
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

    @SuppressWarnings("InstanceofIncompatibleInterface")
    private static @Nullable RPLEBlockBrightness colouredLightBlockFromBlock(@NotNull Block block) {
        if (block instanceof RPLEBlockBrightness)
            return (RPLEBlockBrightness) block;

        return null;
    }

    @SuppressWarnings("InstanceofIncompatibleInterface")
    private static @Nullable RPLEBlockTranslucency colouredTranslucentBlockFromBlock(@NotNull Block block) {
        if (block instanceof RPLEBlockTranslucency)
            return (RPLEBlockTranslucency) block;

        return null;
    }
}
