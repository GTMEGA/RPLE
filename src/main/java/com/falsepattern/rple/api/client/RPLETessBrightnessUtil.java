/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
 */

package com.falsepattern.rple.api.client;

import com.falsepattern.lumi.api.lighting.LightType;
import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.color.DefaultColor;
import com.falsepattern.rple.internal.client.storage.RPLEClientBlockStorage;
import com.falsepattern.rple.internal.common.cache.RPLEBlockStorageRoot;
import com.falsepattern.rple.internal.common.chunk.RPLEChunk;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

import static com.falsepattern.lumi.api.lighting.LightType.BLOCK_LIGHT_TYPE;
import static com.falsepattern.rple.api.client.ClientColorHelper.RGB32_MAX_SKYLIGHT_NO_BLOCKLIGHT;
import static com.falsepattern.rple.api.client.ClientColorHelper.RGB32_NO_SKYLIGHT_NO_BLOCKLIGHT;
import static com.falsepattern.rple.api.common.ServerColorHelper.*;
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
            return CookieMonster.cookieFromRGB32(worldRoot.lumi$hasSky() ? RGB32_MAX_SKYLIGHT_NO_BLOCKLIGHT : RGB32_NO_SKYLIGHT_NO_BLOCKLIGHT);
        }

        if (posY < 0)
            posY = 0;
        val block = world.getBlock(posX, posY, posZ);
        val raw = cbs.rple$getRGBLightValue(block.getUseNeighborBrightness(), posX, posY, posZ);
        val rgb = ClientColorHelper.RGB32ClampMinBlockChannels(raw, minRedBlockLight, minGreenBlockLight, minBlueBlockLight);
        ;
        return CookieMonster.cookieFromRGB32(rgb);
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
        val raw = ClientColorHelper.RGB64FromVanillaRGB(redBrightness,
                                                                                greenBrightness,
                                                                                blueBrightness);
        return CookieMonster.cookieFromRGB64(raw);
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
        final short blockBrightnessColor = RPLEBlock.of(block).rple$getBrightnessColor(world, blockMeta, posX, posY, posZ);
        return createRGBBrightnessForTessellator(BLOCK_LIGHT_TYPE, blockBrightnessColor);
    }

    public static int errorBrightnessForTessellator() {
        final short lightColor = DefaultColor.ERROR.rgb16();
        return createRGBBrightnessForTessellator(red(lightColor), green(lightColor), blue(lightColor));
    }

    public static int createRGBBrightnessForTessellator(short lightColor) {
        return createRGBBrightnessForTessellator(red(lightColor), green(lightColor), blue(lightColor));
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
                                                        short lightColor) {
        return createRGBBrightnessForTessellator(lightType, red(lightColor), green(lightColor), blue(lightColor));
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

    public static int createRGBBrightnessForTessellator(short blockLightColor,
                                                        short skyLightColor) {
        return createRGBBrightnessForTessellator(red(blockLightColor),
                                                 green(blockLightColor),
                                                 blue(blockLightColor),
                                                 red(skyLightColor),
                                                 green(skyLightColor),
                                                 blue(skyLightColor));
    }

    public static int createRGBBrightnessForTessellator(int redBlockLight,
                                                        int greenBlockLight,
                                                        int blueBlockLight,
                                                        int redSkyLight,
                                                        int greenSkyLight,
                                                        int blueSkyLight) {
        final int redBrightness = ClientColorHelper.vanillaFromBlockSky4Bit(redBlockLight,
                                                                                    redSkyLight);
        final int greenBrightness = ClientColorHelper.vanillaFromBlockSky4Bit(greenBlockLight,
                                                                                      greenSkyLight);
        final int blueBrightness = ClientColorHelper.vanillaFromBlockSky4Bit(blueBlockLight,
                                                                                     blueSkyLight);
        final long packedBrightness = ClientColorHelper.RGB64FromVanillaRGB(redBrightness,
                                                                                                    greenBrightness,
                                                                                                    blueBrightness);
        return CookieMonster.cookieFromRGB64(packedBrightness);
    }
}
