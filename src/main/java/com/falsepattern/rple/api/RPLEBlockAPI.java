/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api;

import com.falsepattern.rple.api.block.RPLEColoredBlockRoot;
import com.falsepattern.rple.api.color.RPLEColor;
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

    @SuppressWarnings("ConstantValue")
    public static @NotNull RPLEColor getBlockColoredBrightness(@NotNull Block block) {
        final RPLEColoredBlockRoot rpleBlock = rpleBlockFromBlock(block);
        if (rpleBlock == null)
            return errorColor();

        try {
            final RPLEColor color = rpleBlock.rple$getColoredBrightness();
            if (color != null)
                return color;
        } catch (Exception ignored) {
        }
        return errorColor();
    }

    @SuppressWarnings("ConstantValue")
    public static @NotNull RPLEColor getBlockColoredBrightness(@NotNull Block block, int blockMeta) {
        final RPLEColoredBlockRoot rpleBlock = rpleBlockFromBlock(block);
        if (rpleBlock == null)
            return errorColor();

        try {
            final RPLEColor color = rpleBlock.rple$getColoredBrightness(blockMeta);
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
        final RPLEColoredBlockRoot rpleBlock = rpleBlockFromBlock(block);
        if (rpleBlock == null)
            return errorColor();

        try {
            final RPLEColor color = rpleBlock.rple$getColoredBrightness(world, blockMeta, posX, posY, posZ);
            if (color != null)
                return color;
        } catch (Exception ignored) {
        }
        return errorColor();
    }

    @SuppressWarnings("ConstantValue")
    public static @NotNull RPLEColor getBlockColoredTranslucency(@NotNull Block block) {
        final RPLEColoredBlockRoot rpleBlock = rpleBlockFromBlock(block);
        if (rpleBlock == null)
            return errorColor();

        try {
            final RPLEColor color = rpleBlock.rple$getColoredTranslucency();
            if (color != null)
                return color;
        } catch (Exception ignored) {
        }
        return errorColor();
    }

    @SuppressWarnings("ConstantValue")
    public static @NotNull RPLEColor getBlockColoredTranslucency(@NotNull Block block, int blockMeta) {
        final RPLEColoredBlockRoot rpleBlock = rpleBlockFromBlock(block);
        if (rpleBlock == null)
            return errorColor();

        try {
            final RPLEColor color = rpleBlock.rple$getColoredTranslucency(blockMeta);
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
        final RPLEColoredBlockRoot rpleBlock = rpleBlockFromBlock(block);
        if (rpleBlock == null)
            return errorColor();

        try {
            final RPLEColor color = rpleBlock.rple$getColoredTranslucency(world, blockMeta, posX, posY, posZ);
            if (color != null)
                return color;
        } catch (Exception ignored) {
        }
        return errorColor();
    }

    @SuppressWarnings("InstanceofIncompatibleInterface")
    private static @Nullable RPLEColoredBlockRoot rpleBlockFromBlock(Block block) {
        if (block instanceof RPLEColoredBlockRoot)
            return (RPLEColoredBlockRoot) block;
        return null;
    }
}
