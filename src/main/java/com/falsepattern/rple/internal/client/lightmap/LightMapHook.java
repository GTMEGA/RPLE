/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.client.lightmap;

import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.Compat;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.val;
import lombok.var;
import shadersmod.client.Shaders;

import static com.falsepattern.rple.internal.Common.LIGHT_MAP_1D_SIZE;

@SideOnly(Side.CLIENT)
public final class LightMapHook {
    private static LightMapHook RED_LIGHT_MAP;
    private static LightMapHook GREEN_LIGHT_MAP;
    private static LightMapHook BLUE_LIGHT_MAP;

    public final LightMapTexture texture;

    public LightMapHook(ColorChannel channel) {
        this.texture = LightMapTexture.createLightMapTexture(channel);
    }

    public static void init() {
        RED_LIGHT_MAP = new LightMapHook(ColorChannel.RED_CHANNEL);
        GREEN_LIGHT_MAP = new LightMapHook(ColorChannel.GREEN_CHANNEL);
        BLUE_LIGHT_MAP = new LightMapHook(ColorChannel.BLUE_CHANNEL);
    }

    public static void enableReconfigureAll() {
        if (Compat.shadersEnabled())
            Shaders.enableLightmap();

        RED_LIGHT_MAP.prepare();
        GREEN_LIGHT_MAP.prepare();
        BLUE_LIGHT_MAP.prepare();
    }

    public void prepare() {
        texture.rescale();
        texture.bind();
    }

    public void uploadTexture() {
        texture.upload();
    }

    public void setColor(int blockIndex, int skyIndex, int color) {
        texture.setColor(blockIndex, skyIndex, color);
    }

    public static void updateLightMap(float partialTick) {
        val pixels = LightMapPipeline.lightMapPipeline().updateLightMap(partialTick);

        for (var skyIndex = 0; skyIndex < LIGHT_MAP_1D_SIZE; skyIndex++) {
            for (var blockIndex = 0; blockIndex < LIGHT_MAP_1D_SIZE; blockIndex++) {
                val index = blockIndex + (skyIndex * LightMapConstants.LIGHT_MAP_1D_SIZE);
                val color = pixels[index];
                setColors(blockIndex, skyIndex, color);
            }
        }

        uploadTextures();
    }

    public static void setColors(int blockIndex, int skyIndex, int color) {
        RED_LIGHT_MAP.setColor(blockIndex, skyIndex, color);
        GREEN_LIGHT_MAP.setColor(blockIndex, skyIndex, color);
        BLUE_LIGHT_MAP.setColor(blockIndex, skyIndex, color);
    }

    public static void uploadTextures() {
        RED_LIGHT_MAP.uploadTexture();
        GREEN_LIGHT_MAP.uploadTexture();
        BLUE_LIGHT_MAP.uploadTexture();
    }
}
