/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.api.color.RPLEColor;
import com.falsepattern.rple.internal.common.helper.BrightnessUtil;
import com.falsepattern.rple.internal.common.helper.CookieMonster;
import com.falsepattern.rple.internal.common.helper.EntityHelper;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

import static com.falsepattern.lumina.api.lighting.LightType.BLOCK_LIGHT_TYPE;
import static com.falsepattern.rple.api.RPLEBlockAPI.getBlockColoredBrightness;
import static com.falsepattern.rple.api.RPLEColorAPI.COLOR_MIN;
import static com.falsepattern.rple.api.RPLEColorAPI.errorColor;
import static com.falsepattern.rple.api.RPLEWorldAPI.getWorldRootFromBlockAccess;
import static com.falsepattern.rple.api.color.ColorChannel.*;

@UtilityClass
public final class RPLERenderAPI {
    public static int getRGBBrightnessForTessellator(@NotNull IBlockAccess world, int posX, int posY, int posZ) {
        return getRGBBrightnessForTessellator(world, posX, posY, posZ, 0);
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
        val worldRoot = getWorldRootFromBlockAccess(world);
        if (worldRoot == null)
            return errorBrightnessForTessellator();
        val redBrightness = worldRoot
                .rple$getChannelBrightnessForTessellator(RED_CHANNEL, posX, posY, posZ, minRedBlockLight);
        val greenBrightness = worldRoot
                .rple$getChannelBrightnessForTessellator(GREEN_CHANNEL, posX, posY, posZ, minGreenBlockLight);
        val blueBrightness = worldRoot
                .rple$getChannelBrightnessForTessellator(BLUE_CHANNEL, posX, posY, posZ, minBlueBlockLight);
        val packedBrightness = BrightnessUtil.brightnessesToPackedLong(redBrightness, greenBrightness, blueBrightness);
        return CookieMonster.packedLongToCookie(packedBrightness);
    }

    public static int getChannelLightValueForTessellator(@NotNull IBlockAccess world,
                                                         @NotNull ColorChannel channel,
                                                         LightType lightType,
                                                         int posX,
                                                         int posY,
                                                         int posZ) {
        val worldRoot = getWorldRootFromBlockAccess(world);
        if (worldRoot == null)
            return errorBrightnessForTessellator();
        return worldRoot.rple$getChannelLightValueForTessellator(channel, lightType, posX, posY, posZ);
    }

    public static int getBlockBrightnessForTessellator(@NotNull IBlockAccess world,
                                                       @NotNull Block block,
                                                       int blockMeta,
                                                       int posX,
                                                       int posY,
                                                       int posZ) {
        val blockBrightnessColor = getBlockColoredBrightness(world, block, blockMeta, posX, posY, posZ);
        return createRGBBrightnessForTessellator(BLOCK_LIGHT_TYPE, blockBrightnessColor);
    }

    public static int errorBrightnessForTessellator() {
        val lightValueColor = errorColor();
        val redLightValue = RED_CHANNEL.componentFromColor(lightValueColor);
        val greenLightValue = GREEN_CHANNEL.componentFromColor(lightValueColor);
        val blueLightValue = BLUE_CHANNEL.componentFromColor(lightValueColor);
        return createRGBBrightnessForTessellator(redLightValue, greenLightValue, blueLightValue);
    }

    public static int createRGBBrightnessForTessellator(@NotNull RPLEColor lightValueColor) {
        val redLightValue = RED_CHANNEL.componentFromColor(lightValueColor);
        val greenLightValue = GREEN_CHANNEL.componentFromColor(lightValueColor);
        val blueLightValue = BLUE_CHANNEL.componentFromColor(lightValueColor);
        return createRGBBrightnessForTessellator(redLightValue, greenLightValue, blueLightValue);
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
                                                        @NotNull RPLEColor lightValueColor) {
        val redLightValue = RED_CHANNEL.componentFromColor(lightValueColor);
        val greenLightValue = GREEN_CHANNEL.componentFromColor(lightValueColor);
        val blueLightValue = BLUE_CHANNEL.componentFromColor(lightValueColor);
        return createRGBBrightnessForTessellator(lightType, redLightValue, greenLightValue, blueLightValue);
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
        val redBlockLight = RED_CHANNEL.componentFromColor(blockLightColor);
        val greenBlockLight = GREEN_CHANNEL.componentFromColor(blockLightColor);
        val blueBlockLight = BLUE_CHANNEL.componentFromColor(blockLightColor);
        val redSkyLight = RED_CHANNEL.componentFromColor(skyLightColor);
        val greenSkyLight = GREEN_CHANNEL.componentFromColor(skyLightColor);
        val blueSkyLight = BLUE_CHANNEL.componentFromColor(skyLightColor);
        return createRGBBrightnessForTessellator(redBlockLight,
                                                 greenBlockLight,
                                                 blueBlockLight,
                                                 redSkyLight,
                                                 greenSkyLight,
                                                 blueSkyLight);
    }

    public static int createRGBBrightnessForTessellator(int redBlockLight,
                                                        int greenBlockLight,
                                                        int blueBlockLight,
                                                        int redSkyLight,
                                                        int greenSkyLight,
                                                        int blueSkyLight) {
        val redBrightness = BrightnessUtil.lightLevelsToBrightnessForTessellator(redBlockLight, redSkyLight);
        val greenBrightness = BrightnessUtil.lightLevelsToBrightnessForTessellator(greenBlockLight, greenSkyLight);
        val blueBrightness = BrightnessUtil.lightLevelsToBrightnessForTessellator(blueBlockLight, blueSkyLight);
        val packedBrightness = BrightnessUtil.brightnessesToPackedLong(redBrightness, greenBrightness, blueBrightness);
        return CookieMonster.packedLongToCookie(packedBrightness);
    }

    public static void permit(Class<? extends Entity> entityClass) {
        EntityHelper.permit(entityClass);
    }

    /**
     * By default, any entity that overrides getBrightnessForRender will get a vanilla-style light value from Entity.getBrightnessForRender.
     * You can use this to remove this blocking logic from specific classes. Note: make sure all your superclasses
     * also behave correctly with colored lights!
     *
     * @param entityClassName The fully qualified class name
     */
    public static void permit(String entityClassName) {
        EntityHelper.permit(entityClassName);
    }
}
