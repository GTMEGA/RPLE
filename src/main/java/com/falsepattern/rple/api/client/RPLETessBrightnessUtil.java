/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
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

import com.falsepattern.lib.StableAPI;
import com.falsepattern.lumi.api.lighting.LightType;
import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.color.DefaultColor;
import com.falsepattern.rple.internal.client.storage.RPLEClientBlockStorage;
import com.falsepattern.rple.internal.common.cache.RPLEBlockStorageRoot;
import com.falsepattern.rple.internal.common.chunk.RPLEChunk;
import com.falsepattern.rple.internal.common.chunk.RPLEChunkRoot;
import com.falsepattern.rple.internal.mixin.extension.ExtendedOpenGlHelper;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

import static com.falsepattern.lumi.api.lighting.LightType.BLOCK_LIGHT_TYPE;
import static com.falsepattern.rple.api.client.ClientColorHelper.RGB32_MAX_SKYLIGHT_NO_BLOCKLIGHT;
import static com.falsepattern.rple.api.client.ClientColorHelper.RGB32_NO_SKYLIGHT_NO_BLOCKLIGHT;
import static com.falsepattern.rple.api.common.ServerColorHelper.*;
import static com.falsepattern.rple.api.common.color.ColorChannel.*;

@StableAPI(since = "1.0.0")
public final class RPLETessBrightnessUtil {
    private RPLETessBrightnessUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    //For entity rendering
    @StableAPI.Expose
    public static void setLightMapTextureCoordsRGB64(long rgb64) {
        ExtendedOpenGlHelper.setLightMapTextureCoordsRGB64(rgb64);
    }

    //For entity rendering
    @StableAPI.Expose
    public static long lastLightMapRGB64() {
        return ExtendedOpenGlHelper.lastRGB64();
    }

    @StableAPI.Expose
    public static int getRGBBrightnessForTessellator(@NotNull IBlockAccess world, int posX, int posY, int posZ) {
        return getRGBBrightnessForTessellator(world, posX, posY, posZ, COLOR_MIN);
    }

    @StableAPI.Expose
    public static int getRGBBrightnessForTessellator(@NotNull IBlockAccess world,
                                                     int posX,
                                                     int posY,
                                                     int posZ,
                                                     int minBlockLight) {
        return getRGBBrightnessForTessellator(world, posX, posY, posZ, minBlockLight, minBlockLight, minBlockLight);
    }

    @StableAPI.Expose
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
            return getRGB32BrightnessForTessellatorFastPath(world,
                                                            posX, posY, posZ,
                                                            minRedBlockLight, minGreenBlockLight, minBlueBlockLight);
        } else {
            return getRGB32BrightnessForTessellatorSlowPath(world,
                                                            posX, posY, posZ,
                                                            minRedBlockLight, minGreenBlockLight, minBlueBlockLight);
        }
    }

    @StableAPI.Expose
    private static int getRGB32BrightnessForTessellatorFastPath(@NotNull IBlockAccess world,
                                                                int posX,
                                                                int posY,
                                                                int posZ,
                                                                int minRedBlockLight,
                                                                int minGreenBlockLight,
                                                                int minBlueBlockLight) {
        final RPLEBlockStorageRoot worldRoot = (RPLEBlockStorageRoot) world;
        final RPLEClientBlockStorage cbs = (RPLEClientBlockStorage) world;

        if (posY > 255)
            return CookieMonster.cookieFromRGB32(worldRoot.lumi$hasSky() ? RGB32_MAX_SKYLIGHT_NO_BLOCKLIGHT : RGB32_NO_SKYLIGHT_NO_BLOCKLIGHT);
        if (posY < 0)
            posY = 0;

        final Block block = world.getBlock(posX, posY, posZ);
        final int raw = cbs.rple$getRGBLightValue(block.getUseNeighborBrightness(), posX, posY, posZ);
        final int rgb = ClientColorHelper.RGB32ClampMinBlockChannels(raw, minRedBlockLight, minGreenBlockLight, minBlueBlockLight);

        return CookieMonster.cookieFromRGB32(rgb);
    }

    @StableAPI.Expose
    private static int getRGB32BrightnessForTessellatorSlowPath(@NotNull IBlockAccess world,
                                                                int posX,
                                                                int posY,
                                                                int posZ,
                                                                int minRedBlockLight,
                                                                int minGreenBlockLight,
                                                                int minBlueBlockLight) {
        final RPLEBlockStorageRoot worldRoot = (RPLEBlockStorageRoot) world;
        final RPLEChunkRoot chunk = worldRoot.rple$getChunkRootFromBlockPosIfExists(posX, posZ);

        final RPLEChunk cr = chunk == null ? null : chunk.rple$chunk(RED_CHANNEL);
        final RPLEChunk cg = chunk == null ? null : chunk.rple$chunk(GREEN_CHANNEL);
        final RPLEChunk cb = chunk == null ? null : chunk.rple$chunk(BLUE_CHANNEL);

        final int redBrightness = worldRoot.rple$blockStorage(RED_CHANNEL)
                                           .rple$getChannelBrightnessForTessellator(cr, posX, posY, posZ, minRedBlockLight);
        final int greenBrightness = worldRoot.rple$blockStorage(GREEN_CHANNEL)
                                             .rple$getChannelBrightnessForTessellator(cg, posX, posY, posZ, minGreenBlockLight);
        final int blueBrightness = worldRoot.rple$blockStorage(BLUE_CHANNEL)
                                            .rple$getChannelBrightnessForTessellator(cb, posX, posY, posZ, minBlueBlockLight);

        final long raw = ClientColorHelper.RGB64FromVanillaRGB(redBrightness, greenBrightness, blueBrightness);
        return CookieMonster.cookieFromRGB64(raw);
    }

    @StableAPI.Expose
    public static int getBlockBrightnessForTessellator(@NotNull IBlockAccess world,
                                                       @NotNull Block block,
                                                       int blockMeta,
                                                       int posX,
                                                       int posY,
                                                       int posZ) {
        final short blockBrightnessColor = RPLEBlock.of(block).rple$getBrightnessColor(world, blockMeta, posX, posY, posZ);
        return createRGBBrightnessForTessellator(BLOCK_LIGHT_TYPE, blockBrightnessColor);
    }

    @StableAPI.Expose
    public static int errorBrightnessForTessellator() {
        final short lightColor = DefaultColor.ERROR.rgb16();
        return createRGBBrightnessForTessellator(red(lightColor), green(lightColor), blue(lightColor));
    }

    @StableAPI.Expose
    public static int createRGBBrightnessForTessellator(short lightColor) {
        return createRGBBrightnessForTessellator(red(lightColor), green(lightColor), blue(lightColor));
    }

    @StableAPI.Expose
    public static int createRGBBrightnessForTessellator(int redLightValue, int greenLightValue, int blueLightValue) {
        return createRGBBrightnessForTessellator(redLightValue,
                                                 greenLightValue,
                                                 blueLightValue,
                                                 redLightValue,
                                                 greenLightValue,
                                                 blueLightValue);
    }

    @StableAPI.Expose
    public static int createRGBBrightnessForTessellator(@NotNull LightType lightType,
                                                        short lightColor) {
        return createRGBBrightnessForTessellator(lightType, red(lightColor), green(lightColor), blue(lightColor));
    }

    @StableAPI.Expose
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

    @StableAPI.Expose
    public static int createRGBBrightnessForTessellator(short blockLightColor,
                                                        short skyLightColor) {
        return createRGBBrightnessForTessellator(red(blockLightColor),
                                                 green(blockLightColor),
                                                 blue(blockLightColor),
                                                 red(skyLightColor),
                                                 green(skyLightColor),
                                                 blue(skyLightColor));
    }

    @StableAPI.Expose
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
        final long rgb64 = ClientColorHelper.RGB64FromVanillaRGB(redBrightness,
                                                                                                    greenBrightness,
                                                                                                    blueBrightness);
        return CookieMonster.cookieFromRGB64(rgb64);
    }
}
