/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.hook;

import com.falsepattern.rple.api.client.CookieMonster;
import com.falsepattern.rple.api.client.ClientColorHelper;
import com.falsepattern.rple.api.client.RPLETessBrightnessUtil;
import com.falsepattern.rple.api.common.ServerColorHelper;
import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.internal.Compat;
import com.falsepattern.rple.internal.client.optifine.ColorDynamicLights;
import com.falsepattern.rple.internal.client.render.EntityColorHandler;
import com.falsepattern.rple.internal.common.chunk.RPLESubChunkRoot;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import static com.falsepattern.rple.api.common.ServerColorHelper.lightOpacityFromRGB16;
import static com.falsepattern.rple.api.common.ServerColorHelper.lightValueFromRGB16;
import static com.falsepattern.rple.api.common.color.ColorChannel.*;

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
        return ServerColorHelper.maxColorComponent(redLightValue, greenLightValue, blueLightValue);
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
        return ServerColorHelper.maxColorComponent(redLightValue, greenLightValue, blueLightValue);
    }

    public static int getLightValue(Block block) {
        val rpleBlock = RPLEBlock.of(block);
        val color = rpleBlock.rple$getBrightnessColor();
        return lightValueFromRGB16(color);
    }

    public static int getLightValue(IBlockAccess world,
                                    Block block,
                                    int posX,
                                    int posY,
                                    int posZ) {
        val rpleBlock = RPLEBlock.of(block);
        val blockMeta = world.getBlockMetadata(posX, posY, posZ);
        val color = rpleBlock.rple$getBrightnessColor(world, blockMeta, posX, posY, posZ);
        return lightValueFromRGB16(color);
    }

    public static int getLightOpacity(Block block) {
        val rpleBlock = RPLEBlock.of(block);
        val color = rpleBlock.rple$getTranslucencyColor();
        return lightOpacityFromRGB16(color);
    }

    public static int getLightOpacity(IBlockAccess world,
                                      Block block,
                                      int posX,
                                      int posY,
                                      int posZ) {
        val rpleBlock = RPLEBlock.of(block);
        val blockMeta = world.getBlockMetadata(posX, posY, posZ);
        val color = rpleBlock.rple$getTranslucencyColor(world, blockMeta, posX, posY, posZ);
        return lightOpacityFromRGB16(color);
    }

    @SideOnly(Side.CLIENT)
    public static int getEntityRGBBrightnessForTessellator(Entity entity,
                                                           IBlockAccess world,
                                                           int posX,
                                                           int posY,
                                                           int posZ,
                                                           int minBrightnessCookie) {
        int brightness = getRGBBrightnessForTessellator(world, posX, posY, posZ, minBrightnessCookie);
        if (Compat.dynamicLightsEnabled()) {
            brightness = ColorDynamicLights.getCombinedLight(posX, posY, posZ, brightness);
        }
        if (EntityColorHandler.isOnBlockList(entity.getClass())) {
            val packedBrightness = CookieMonster.RGB64FromCookie(brightness);
            brightness = ClientColorHelper.vanillaFromRGB64Max(packedBrightness);
        }
        return brightness;
    }

    @SideOnly(Side.CLIENT)
    public static int getBlockRGBBrightnessForTessellator(IBlockAccess world,
                                                          int posX,
                                                          int posY,
                                                          int posZ) {
        var block = world.getBlock(posX, posY, posZ);
        var blockMeta = world.getBlockMetadata(posX, posY, posZ);

        var brightnessCookie = RPLETessBrightnessUtil.getBlockBrightnessForTessellator(world,
                                                                                       block,
                                                                                       blockMeta,
                                                                                       posX,
                                                                                       posY,
                                                                                       posZ);
        brightnessCookie = getRGBBrightnessForTessellator(world, posX, posY, posZ, brightnessCookie);

        if (brightnessCookie == 0 && block instanceof BlockSlab) {
            --posY;

            block = world.getBlock(posX, posY, posZ);
            blockMeta = world.getBlockMetadata(posX, posY, posZ);

            brightnessCookie = RPLETessBrightnessUtil.getBlockBrightnessForTessellator(world,
                                                                                       block,
                                                                                       blockMeta,
                                                                                       posX,
                                                                                       posY,
                                                                                       posZ);
            brightnessCookie = getRGBBrightnessForTessellator(world, posX, posY, posZ, brightnessCookie);
        }

        return brightnessCookie;
    }

    @SideOnly(Side.CLIENT)
    public static int getBlockFluidRGBBrightnessForTessellator(IBlockAccess world,
                                                               int posX,
                                                               int posY,
                                                               int posZ) {
        val brightness = getRGBBrightnessForTessellator(world, posX, posY, posZ);
        val topBrightness = getRGBBrightnessForTessellator(world, posX, posY + 1, posZ);
        return ClientColorHelper.cookieMax(brightness, topBrightness);
    }

    @SideOnly(Side.CLIENT)
    public static int getRGBBrightnessForTessellator(IBlockAccess world, int posX, int posY, int posZ) {
        return RPLETessBrightnessUtil.getRGBBrightnessForTessellator(world, posX, posY, posZ);
    }

    @SideOnly(Side.CLIENT)
    public static int getRGBBrightnessForTessellator(IBlockAccess world,
                                                     int posX,
                                                     int posY,
                                                     int posZ,
                                                     int minBrightnessCookie) {
        val packedMinBrightness = CookieMonster.RGB64FromCookie(minBrightnessCookie);

        val minRedBrightness = ClientColorHelper.vanillaFromRGB64Red(packedMinBrightness);
        val minGreenBrightness = ClientColorHelper.vanillaFromRGB64Green(packedMinBrightness);
        val minBlueBrightness = ClientColorHelper.vanillaFromRGB64Blue(packedMinBrightness);

        val minRedBlockLight = ClientColorHelper.block4BitFromVanilla(minRedBrightness);
        val minGreenBlockLight = ClientColorHelper.block4BitFromVanilla(minGreenBrightness);
        val minBlueBlockLight = ClientColorHelper.block4BitFromVanilla(minBlueBrightness);

        return RPLETessBrightnessUtil.getRGBBrightnessForTessellator(world,
                                                                     posX,
                                                                     posY,
                                                                     posZ,
                                                                     minRedBlockLight,
                                                                     minGreenBlockLight,
                                                                     minBlueBlockLight);
    }
}
