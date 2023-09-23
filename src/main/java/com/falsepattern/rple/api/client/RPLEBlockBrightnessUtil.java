/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.client;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.rple.api.common.color.RPLEColor;
import com.falsepattern.rple.internal.client.render.CookieMonster;
import com.falsepattern.rple.internal.client.render.TessellatorBrightnessHelper;
import com.falsepattern.rple.internal.common.storage.RPLEBlockStorageRoot;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

import static com.falsepattern.lumina.api.lighting.LightType.BLOCK_LIGHT_TYPE;
import static com.falsepattern.rple.api.common.RPLEBlockUtil.getBlockColoredBrightness;
import static com.falsepattern.rple.api.common.RPLEColorUtil.COLOR_MIN;
import static com.falsepattern.rple.api.common.RPLEColorUtil.errorColor;
import static com.falsepattern.rple.api.common.color.ColorChannel.*;

public final class RPLEBlockBrightnessUtil {
    private RPLEBlockBrightnessUtil() {
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

        final RPLEBlockStorageRoot worldRoot = (RPLEBlockStorageRoot) world;
        final int redBrightness = worldRoot.rple$blockStorage(RED_CHANNEL)
                                           .rple$getChannelBrightnessForTessellator(posX, posY, posZ, minRedBlockLight);
        final int greenBrightness = worldRoot.rple$blockStorage(GREEN_CHANNEL)
                                             .rple$getChannelBrightnessForTessellator(posX, posY, posZ, minGreenBlockLight);
        final int blueBrightness = worldRoot.rple$blockStorage(BLUE_CHANNEL)
                                            .rple$getChannelBrightnessForTessellator(posX, posY, posZ, minBlueBlockLight);
        final long packedBrightness = TessellatorBrightnessHelper.packedBrightnessFromTessellatorBrightnessChannels(redBrightness,
                                                                                                                    greenBrightness,
                                                                                                                    blueBrightness);
        return CookieMonster.packedLongToCookie(packedBrightness);
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
        final RPLEColor blockBrightnessColor = getBlockColoredBrightness(world, block, blockMeta, posX, posY, posZ);
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
        final int redBrightness = TessellatorBrightnessHelper.lightLevelsToBrightnessForTessellator(redBlockLight,
                                                                                                    redSkyLight);
        final int greenBrightness = TessellatorBrightnessHelper.lightLevelsToBrightnessForTessellator(greenBlockLight,
                                                                                                      greenSkyLight);
        final int blueBrightness = TessellatorBrightnessHelper.lightLevelsToBrightnessForTessellator(blueBlockLight,
                                                                                                     blueSkyLight);
        final long packedBrightness = TessellatorBrightnessHelper.packedBrightnessFromTessellatorBrightnessChannels(redBrightness,
                                                                                                                    greenBrightness,
                                                                                                                    blueBrightness);
        return CookieMonster.packedLongToCookie(packedBrightness);
    }
}
