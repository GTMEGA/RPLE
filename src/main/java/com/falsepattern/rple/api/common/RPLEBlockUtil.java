/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common;

import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.color.RPLEColor;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.falsepattern.rple.api.common.RPLEColorUtil.errorColor;

@SuppressWarnings("unused")
public final class RPLEBlockUtil {
    private RPLEBlockUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @SuppressWarnings("ConstantValue")
    public static @NotNull RPLEColor getBlockColoredBrightness(@NotNull Block block) {
        final RPLEBlock rpleBlock = rpleBlockFromBlock(block);
        if (rpleBlock == null)
            return errorColor();

        try {
            final RPLEColor color = rpleBlock.rple$getBrightnessColor();
            if (color != null)
                return color;
        } catch (Exception ignored) {
        }
        return errorColor();
    }

    @SuppressWarnings("ConstantValue")
    public static @NotNull RPLEColor getBlockColoredBrightness(@NotNull Block block, int blockMeta) {
        final RPLEBlock rpleBlock = rpleBlockFromBlock(block);
        if (rpleBlock == null)
            return errorColor();

        try {
            final RPLEColor color = rpleBlock.rple$getBrightnessColor(blockMeta);
            if (color != null)
                return color;
        } catch (Exception ignored) {
        }
        return errorColor();
    }

    @SuppressWarnings("ConstantValue")
    public static @NotNull RPLEColor getBlockColoredBrightness(@NotNull IBlockAccess world,
                                                               @NotNull Block block,
                                                               int blockMeta,
                                                               int posX,
                                                               int posY,
                                                               int posZ) {
        final RPLEBlock rpleBlock = rpleBlockFromBlock(block);
        if (rpleBlock == null)
            return errorColor();

        try {
            final RPLEColor color = rpleBlock.rple$getBrightnessColor(world, blockMeta, posX, posY, posZ);
            if (color != null)
                return color;
        } catch (Exception ignored) {
        }
        return errorColor();
    }

    @SuppressWarnings("ConstantValue")
    public static @NotNull RPLEColor getBlockColoredTranslucency(@NotNull Block block) {
        final RPLEBlock rpleBlock = rpleBlockFromBlock(block);
        if (rpleBlock == null)
            return errorColor();

        try {
            final RPLEColor color = rpleBlock.rple$getTranslucencyColor();
            if (color != null)
                return color;
        } catch (Exception ignored) {
        }
        return errorColor();
    }

    @SuppressWarnings("ConstantValue")
    public static @NotNull RPLEColor getBlockColoredTranslucency(@NotNull Block block, int blockMeta) {
        final RPLEBlock rpleBlock = rpleBlockFromBlock(block);
        if (rpleBlock == null)
            return errorColor();

        try {
            final RPLEColor color = rpleBlock.rple$getTranslucencyColor(blockMeta);
            if (color != null)
                return color;
        } catch (Exception ignored) {
        }
        return errorColor();
    }

    @SuppressWarnings("ConstantValue")
    public static @NotNull RPLEColor getBlockColoredTranslucency(@NotNull IBlockAccess world,
                                                                 @NotNull Block block,
                                                                 int blockMeta,
                                                                 int posX,
                                                                 int posY,
                                                                 int posZ) {
        final RPLEBlock rpleBlock = rpleBlockFromBlock(block);
        if (rpleBlock == null)
            return errorColor();

        try {
            final RPLEColor color = rpleBlock.rple$getTranslucencyColor(world, blockMeta, posX, posY, posZ);
            if (color != null)
                return color;
        } catch (Exception ignored) {
        }
        return errorColor();
    }

    @SuppressWarnings("InstanceofIncompatibleInterface")
    private static @Nullable RPLEBlock rpleBlockFromBlock(Block block) {
        if (block instanceof RPLEBlock)
            return (RPLEBlock) block;
        return null;
    }
}
