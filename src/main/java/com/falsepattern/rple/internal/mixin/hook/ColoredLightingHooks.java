/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.hook;

import com.falsepattern.rple.api.RPLEColorAPI;
import com.falsepattern.rple.api.RPLERenderAPI;
import com.falsepattern.rple.api.block.RPLEBlock;
import com.falsepattern.rple.internal.common.chunk.RPLESubChunkRoot;
import com.falsepattern.rple.internal.common.helper.BrightnessUtil;
import com.falsepattern.rple.internal.common.helper.CookieMonster;
import com.falsepattern.rple.internal.common.helper.CookieWrappers;
import com.falsepattern.rple.internal.common.helper.EntityHelper;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import static com.falsepattern.rple.api.RPLEColorAPI.lightOpacityFromColor;
import static com.falsepattern.rple.api.RPLEColorAPI.lightValueFromColor;
import static com.falsepattern.rple.api.color.ColorChannel.*;

@UtilityClass
public final class ColoredLightingHooks {
    @SuppressWarnings("CastToIncompatibleInterface")
    public static int getMaxBlockLightValue(ExtendedBlockStorage subChunkBase,
                                            int subChunkPosX,
                                            int subChunkPosY,
                                            int subChunkPosZ) {
        val subChunkRoot = (RPLESubChunkRoot) subChunkBase;
        val redLightValue = subChunkRoot.rple$subChunk(RED_CHANNEL)
                                        .lumi$getBlockLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
        val greenLightValue = subChunkRoot.rple$subChunk(GREEN_CHANNEL)
                                          .lumi$getBlockLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
        val blueLightValue = subChunkRoot.rple$subChunk(BLUE_CHANNEL)
                                         .lumi$getBlockLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
        return RPLEColorAPI.maxColorComponent(redLightValue, greenLightValue, blueLightValue);
    }

    @SuppressWarnings("CastToIncompatibleInterface")
    public static int getMaxSkyLightValue(ExtendedBlockStorage subChunkBase,
                                          int subChunkPosX,
                                          int subChunkPosY,
                                          int subChunkPosZ) {
        val subChunkRoot = (RPLESubChunkRoot) subChunkBase;
        val redLightValue = subChunkRoot.rple$subChunk(RED_CHANNEL)
                                        .lumi$getSkyLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
        val greenLightValue = subChunkRoot.rple$subChunk(GREEN_CHANNEL)
                                          .lumi$getSkyLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
        val blueLightValue = subChunkRoot.rple$subChunk(BLUE_CHANNEL)
                                         .lumi$getSkyLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
        return RPLEColorAPI.maxColorComponent(redLightValue, greenLightValue, blueLightValue);
    }

    public static int getLightValue(Block block) {
        val rpleBlock = (RPLEBlock) block;
        val color = rpleBlock.rple$getBrightnessColor();
        return lightValueFromColor(color);
    }

    public static int getLightValue(IBlockAccess world,
                                    Block block,
                                    int posX,
                                    int posY,
                                    int posZ) {
        val rpleBlock = (RPLEBlock) block;
        val blockMeta = world.getBlockMetadata(posX, posY, posZ);
        val color = rpleBlock.rple$getBrightnessColor(world, blockMeta, posX, posY, posZ);
        return lightValueFromColor(color);
    }

    public static int getLightOpacity(Block block) {
        val rpleBlock = (RPLEBlock) block;
        val color = rpleBlock.rple$getTranslucencyColor();
        return lightOpacityFromColor(color);
    }

    public static int getLightOpacity(IBlockAccess world,
                                      Block block,
                                      int posX,
                                      int posY,
                                      int posZ) {
        val rpleBlock = (RPLEBlock) block;
        val blockMeta = world.getBlockMetadata(posX, posY, posZ);
        val color = rpleBlock.rple$getTranslucencyColor(world, blockMeta, posX, posY, posZ);
        return lightOpacityFromColor(color);
    }

    public static int getEntityRGBBrightnessForTessellator(Entity entity,
                                                           IBlockAccess world,
                                                           int posX,
                                                           int posY,
                                                           int posZ,
                                                           int cookieMinBlockLight) {
        var brightness = getRGBBrightnessForTessellator(world, posX, posY, posZ, cookieMinBlockLight);
        if (EntityHelper.isOnBlockList(entity.getClass())) {
            val packedBrightness = CookieMonster.cookieToPackedLong(brightness);
            brightness = BrightnessUtil.getBrightestChannelFromPacked(packedBrightness);
        }
        return brightness;
    }

    public static int getBlockRGBBrightnessForTessellator(IBlockAccess world,
                                                          int posX,
                                                          int posY,
                                                          int posZ) {
        var block = world.getBlock(posX, posY, posZ);
        var blockMeta = world.getBlockMetadata(posX, posY, posZ);

        var brightness = RPLERenderAPI.getBlockBrightnessForTessellator(world, block, blockMeta, posX, posY, posZ);
        brightness = getRGBBrightnessForTessellator(world, posX, posY, posZ, brightness);

        if (brightness == 0 && block instanceof BlockSlab) {
            --posY;

            block = world.getBlock(posX, posY, posZ);
            blockMeta = world.getBlockMetadata(posX, posY, posZ);

            brightness = RPLERenderAPI.getBlockBrightnessForTessellator(world, block, blockMeta, posX, posY, posZ);
            brightness = getRGBBrightnessForTessellator(world, posX, posY, posZ, brightness);
        }

        return brightness;
    }

    public static int getBlockFluidRGBBrightnessForTessellator(IBlockAccess world,
                                                               int posX,
                                                               int posY,
                                                               int posZ) {
        val brightness = getRGBBrightnessForTessellator(world, posX, posY, posZ, 0);
        val topBrightness = getRGBBrightnessForTessellator(world, posX, posY + 1, posZ, 0);
        return CookieWrappers.packedMax(brightness, topBrightness);
    }

    public static int getRGBBrightnessForTessellator(IBlockAccess world,
                                                     int posX,
                                                     int posY,
                                                     int posZ,
                                                     int cookieMinBlockLight) {
        val packedMinBrightness = CookieMonster.cookieToPackedLong(cookieMinBlockLight);

        val minRedBrightness = BrightnessUtil.getBrightnessRed(packedMinBrightness);
        val minGreenBrightness = BrightnessUtil.getBrightnessGreen(packedMinBrightness);
        val minBlueBrightness = BrightnessUtil.getBrightnessBlue(packedMinBrightness);

        val minRedBlockLight = BrightnessUtil.getBlockLightFromBrightness(minRedBrightness);
        val minGreenBlockLight = BrightnessUtil.getBlockLightFromBrightness(minGreenBrightness);
        val minBlueBlockLight = BrightnessUtil.getBlockLightFromBrightness(minBlueBrightness);

        return RPLERenderAPI.getRGBBrightnessForTessellator(world,
                                                            posX,
                                                            posY,
                                                            posZ,
                                                            minRedBlockLight,
                                                            minGreenBlockLight,
                                                            minBlueBlockLight);
    }
}
