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
import shadersmod.client.Shaders;

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

    public static void updateLightMap(float partialTick) {
        val pixels = LightMapPipeline.lightMapPipeline().updateLightMap(partialTick);

        RED_LIGHT_MAP.texture.update(pixels);
        GREEN_LIGHT_MAP.texture.update(pixels);
        BLUE_LIGHT_MAP.texture.update(pixels);
    }
}
