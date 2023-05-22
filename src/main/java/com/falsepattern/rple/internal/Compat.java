/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal;

import codechicken.multipart.BlockMultipart;
import com.falsepattern.rple.internal.stubpackage.GlStateManager;
import shadersmod.client.Shaders;

import net.minecraft.block.Block;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Loader;

public class Compat {
    private static Boolean multipartPresent;
    private static Boolean projRedLightsPresent;
    public static boolean shadersEnabled() {
        return FMLClientHandler.instance().hasOptifine() && Shaders.shaderPackLoaded;
    }

    public static void toggleLightMapShaders(boolean state) {
        ShadersCompat.toggleLightMap(state);
    }

    public static void optiFineSetActiveTexture(int texture) {
        ShadersCompat.setActiveTexture(texture);
    }

    public static boolean isMultipart(Block block) {
        if (multipartPresent == null) {
            multipartPresent = Loader.isModLoaded("ForgeMultipart");
        }
        if (multipartPresent) {
            return MultipartCompat.isMultipart(block);
        }
        return false;
    }

    public static boolean projRedLightsPresent() {
        if (projRedLightsPresent == null) {
            projRedLightsPresent = Loader.isModLoaded("ProjRed|Illumination");
        }
        return projRedLightsPresent;
    }

    private static class MultipartCompat {
        public static boolean isMultipart(Block block) {
            return block instanceof BlockMultipart;
        }
    }

    private static class ShadersCompat {
        public static void toggleLightMap(boolean state) {
            if (state)
                Shaders.enableLightmap();
            else
                Shaders.disableLightmap();
        }

        public static void setActiveTexture(int texture) {
            GlStateManager.activeTextureUnit = texture;
        }
    }
}
