/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal;

import com.falsepattern.falsetweaks.api.ThreadedChunkUpdates;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.experimental.UtilityClass;
import lombok.var;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.launchwrapper.Launch;
import shadersmod.client.Shaders;
import stubpackage.Config;
import stubpackage.GlStateManager;

import java.io.IOException;

@UtilityClass
public final class Compat {
    private static final boolean IS_SHADERS_MOD_PRESENT;
    private static final boolean IS_DYNAMICLIGHTS_PRESENT;
    private static final boolean IS_FALSETWEAKS_PRESENT;

    static {
        var shadersModPresent = false;
        try {
            shadersModPresent = Launch.classLoader.getClassBytes("shadersmod.client.Shaders") != null;
        } catch (IOException ignored) {
        }
        IS_SHADERS_MOD_PRESENT = shadersModPresent;
        var dynLightsPresent = false;
        try {
            dynLightsPresent = Launch.classLoader.getClassBytes("DynamicLights") != null;
        } catch (IOException ignored) {
        }
        IS_DYNAMICLIGHTS_PRESENT = dynLightsPresent;
        var falseTweaksPresent = false;
        try {
            falseTweaksPresent = Launch.classLoader.getClassBytes("com.falsepattern.falsetweaks.FalseTweaks") != null;
        } catch (IOException ignored) {
        }
        IS_FALSETWEAKS_PRESENT = falseTweaksPresent;
    }

    public static boolean falseTweaksThreadedChunksEnabled() {
        if (IS_FALSETWEAKS_PRESENT)
            return ThreadedChunkUpdates.isEnabled();
        return false;
    }

    @SideOnly(Side.CLIENT)
    public static boolean shadersEnabled() {
        return IS_SHADERS_MOD_PRESENT && ShadersCompat.shaderPackLoaded();
    }

    @SideOnly(Side.CLIENT)
    public static boolean dynamicLightsEnabled() {
        return IS_DYNAMICLIGHTS_PRESENT && Config.isDynamicLights();
    }

    @SideOnly(Side.CLIENT)
    public static void toggleLightMapShaders(boolean state) {
        ShadersCompat.toggleLightMap(state);
    }

    @SideOnly(Side.CLIENT)
    public static void optiFineSetActiveTexture(int texture) {
        ShadersCompat.setActiveTexture(texture);
    }

    @SideOnly(Side.CLIENT)
    public static Tessellator tessellator() {
        if (ThreadedChunkUpdates.isEnabled()) {
            return ThreadedChunkUpdates.getThreadTessellator();
        }
        return Tessellator.instance;
    }

    private static class ShadersCompat {
        public static void toggleLightMap(boolean state) {
            if (state) {
                Shaders.enableLightmap();
            } else {
                Shaders.disableLightmap();
            }
        }

        public static void setActiveTexture(int texture) {
            GlStateManager.activeTextureUnit = texture;
        }

        public static boolean shaderPackLoaded() {
            return Shaders.shaderPackLoaded;
        }
    }
}
