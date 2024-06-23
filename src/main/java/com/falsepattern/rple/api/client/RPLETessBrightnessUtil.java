/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.client;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.color.RPLEColor;
import com.falsepattern.rple.internal.client.storage.RPLEClientBlockStorage;
import com.falsepattern.rple.internal.common.cache.RPLEBlockStorageRoot;
import com.falsepattern.rple.internal.common.chunk.RPLEChunk;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

import static com.falsepattern.lumina.api.lighting.LightType.BLOCK_LIGHT_TYPE;
import static com.falsepattern.rple.api.client.RGB32Helper.RGB_MAX_SKYLIGHT_NO_BLOCKLIGHT;
import static com.falsepattern.rple.api.client.RGB32Helper.RGB_NO_SKYLIGHT_NO_BLOCKLIGHT;
import static com.falsepattern.rple.api.common.RPLEColorUtil.COLOR_MIN;
import static com.falsepattern.rple.api.common.RPLEColorUtil.errorColor;
import static com.falsepattern.rple.api.common.color.ColorChannel.*;

public final class RPLETessBrightnessUtil {
    private RPLETessBrightnessUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static int getRGBBrightnessForTessellator(@NotNull IBlockAccess world, int posX, int posY, int posZ) {
        return getRGBBrightnessForTessellator(world, posX, posY, posZ, COLOR_MIN);
    }

    public static int getRGBBrightnessForTessellator(@NotNull IBlockAccess world,
                                                     int posX,
                                                     int posY,
                                                     int posZ,
                                                     int minBlockLight) {
        return getRGBBrightnessForTessellator(world, posX, posY, posZ, minBlockLight, minBlockLight, minBlockLight);
    }

    public static int getRGBBrightnessForTessellator(@NotNull IBlockAccess world,
                                                     int posX,
                                                     int posY,
                                                     int posZ,
                                                     int minRedBlockLight,
                                                     int minGreenBlockLight,
                                                     int minBlueBlockLight) {
        if (!(world instanceof RPLEBlockStorageRoot))
            return errorBrightnessForTessellator();

        if (world instanceof RPLEClientBlockStorage) {
            return getPackedRGBBrightnessForTessellatorFastPath(world,
                                                                posX, posY, posZ,
                                                                minRedBlockLight, minGreenBlockLight, minBlueBlockLight);
        } else {
            return getPackedRGBBrightnessForTessellatorSlowPath(world,
                                                                posX, posY, posZ,
                                                                minRedBlockLight, minGreenBlockLight, minBlueBlockLight);
        }
    }

    private static int getPackedRGBBrightnessForTessellatorFastPath(@NotNull IBlockAccess world,
                                                                    int posX,
                                                                    int posY,
                                                                    int posZ,
                                                                    int minRedBlockLight,
                                                                    int minGreenBlockLight,
                                                                    int minBlueBlockLight) {
        val worldRoot = (RPLEBlockStorageRoot) world;
        val cbs = (RPLEClientBlockStorage) world;
        if (posY > 255) {
            return CookieMonster.rgbToCookie(worldRoot.lumi$hasSky() ? RGB_MAX_SKYLIGHT_NO_BLOCKLIGHT : RGB_NO_SKYLIGHT_NO_BLOCKLIGHT);
        }

        if (posY < 0)
            posY = 0;
        val block = world.getBlock(posX, posY, posZ);
        val raw = cbs.rple$getRGBLightValue(block.getUseNeighborBrightness(), posX, posY, posZ);
        val rgb = RGB32Helper.weaveMinBlockLightLevels(raw, minRedBlockLight, minGreenBlockLight, minBlueBlockLight);
        ;
        return CookieMonster.rgbToCookie(rgb);
    }

    private static int getPackedRGBBrightnessForTessellatorSlowPath(@NotNull IBlockAccess world,
                                                                    int posX,
                                                                    int posY,
                                                                    int posZ,
                                                                    int minRedBlockLight,
                                                                    int minGreenBlockLight,
                                                                    int minBlueBlockLight) {
        val worldRoot = (RPLEBlockStorageRoot) world;

        val chunk = worldRoot.rple$getChunkRootFromBlockPosIfExists(posX, posZ);
        val cr = (RPLEChunk) (chunk == null ? null : chunk.rple$chunk(RED_CHANNEL));
        val cg = (RPLEChunk) (chunk == null ? null : chunk.rple$chunk(GREEN_CHANNEL));
        val cb = (RPLEChunk) (chunk == null ? null : chunk.rple$chunk(BLUE_CHANNEL));
        final int redBrightness = worldRoot.rple$blockStorage(RED_CHANNEL)
                                           .rple$getChannelBrightnessForTessellator(cr, posX, posY, posZ, minRedBlockLight);
        final int greenBrightness = worldRoot.rple$blockStorage(GREEN_CHANNEL)
                                             .rple$getChannelBrightnessForTessellator(cg, posX, posY, posZ, minGreenBlockLight);
        final int blueBrightness = worldRoot.rple$blockStorage(BLUE_CHANNEL)
                                            .rple$getChannelBrightnessForTessellator(cb, posX, posY, posZ, minBlueBlockLight);
        val raw = RGB64Helper.packedBrightnessFromTessellatorBrightnessChannels(redBrightness,
                                                                                greenBrightness,
                                                                                blueBrightness);
        return CookieMonster.packedLongToCookie(raw);
    }

//    public static int getChannelLightValueForTessellator(@NotNull IBlockAccess world,
//                                                         @NotNull ColorChannel channel,
//                                                         @NotNull LightType lightType,
//                                                         int posX,
//                                                         int posY,
//                                                         int posZ) {
//        final RPLEWorldRoot worldRoot = rple$getWorldRootFromBlockAccess(world);
//        if (worldRoot == null)
//            return errorBrightnessForTessellator();
//        return worldRoot.rple$getChannelLightValueForTessellator(channel, lightType, posX, posY, posZ);
//    }

    public static int getBlockBrightnessForTessellator(@NotNull IBlockAccess world,
                                                       @NotNull Block block,
                                                       int blockMeta,
                                                       int posX,
                                                       int posY,
                                                       int posZ) {
        final RPLEColor blockBrightnessColor = RPLEBlock.of(block).rple$getBrightnessColor(world, blockMeta, posX, posY, posZ);
        return createRGBBrightnessForTessellator(BLOCK_LIGHT_TYPE, blockBrightnessColor);
    }

    public static int errorBrightnessForTessellator() {
        final RPLEColor lightColor = errorColor();
        return createRGBBrightnessForTessellator(lightColor.red(), lightColor.green(), lightColor.blue());
    }

    public static int createRGBBrightnessForTessellator(@NotNull RPLEColor lightColor) {
        return createRGBBrightnessForTessellator(lightColor.red(), lightColor.green(), lightColor.blue());
    }

    public static int createRGBBrightnessForTessellator(int redLightValue, int greenLightValue, int blueLightValue) {
        return createRGBBrightnessForTessellator(redLightValue,
                                                 greenLightValue,
                                                 blueLightValue,
                                                 redLightValue,
                                                 greenLightValue,
                                                 blueLightValue);
    }

    public static int createRGBBrightnessForTessellator(@NotNull LightType lightType,
                                                        @NotNull RPLEColor lightColor) {
        return createRGBBrightnessForTessellator(lightType, lightColor.red(), lightColor.green(), lightColor.blue());
    }

    public static int createRGBBrightnessForTessellator(@NotNull LightType lightType,
                                                        int redLightValue,
                                                        int greenLightValue,
                                                        int blueLightValue) {
        switch (lightType) {
            default:
            case BLOCK_LIGHT_TYPE:
                return createRGBBrightnessForTessellator(redLightValue,
                                                         greenLightValue,
                                                         blueLightValue,
                                                         COLOR_MIN,
                                                         COLOR_MIN,
                                                         COLOR_MIN);
            case SKY_LIGHT_TYPE:
                return createRGBBrightnessForTessellator(COLOR_MIN,
                                                         COLOR_MIN,
                                                         COLOR_MIN,
                                                         redLightValue,
                                                         greenLightValue,
                                                         blueLightValue);
        }
    }

    public static int createRGBBrightnessForTessellator(@NotNull RPLEColor blockLightColor,
                                                        @NotNull RPLEColor skyLightColor) {
        return createRGBBrightnessForTessellator(blockLightColor.red(),
                                                 blockLightColor.green(),
                                                 blockLightColor.blue(),
                                                 skyLightColor.red(),
                                                 skyLightColor.green(),
                                                 skyLightColor.blue());
    }

    public static int createRGBBrightnessForTessellator(int redBlockLight,
                                                        int greenBlockLight,
                                                        int blueBlockLight,
                                                        int redSkyLight,
                                                        int greenSkyLight,
                                                        int blueSkyLight) {
        final int redBrightness = RGB64Helper.lightLevelsToBrightnessForTessellator(redBlockLight,
                                                                                    redSkyLight);
        final int greenBrightness = RGB64Helper.lightLevelsToBrightnessForTessellator(greenBlockLight,
                                                                                      greenSkyLight);
        final int blueBrightness = RGB64Helper.lightLevelsToBrightnessForTessellator(blueBlockLight,
                                                                                     blueSkyLight);
        final long packedBrightness = RGB64Helper.packedBrightnessFromTessellatorBrightnessChannels(redBrightness,
                                                                                                    greenBrightness,
                                                                                                    blueBrightness);
        return CookieMonster.packedLongToCookie(packedBrightness);
    }
}
