/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal;

import lombok.experimental.UtilityClass;
import lombok.var;
import net.minecraft.launchwrapper.Launch;
import shadersmod.client.Shaders;
import stubpackage.GlStateManager;

import java.io.IOException;

@UtilityClass
public final class Compat {
    private static final boolean IS_SHADERS_MOD_PRESENT;

    static {
        var shadersModPresent = false;
        try {
            shadersModPresent = Launch.classLoader.getClassBytes("shadersmod.client.Shaders") != null;
        } catch (IOException ignored) {
        }
        IS_SHADERS_MOD_PRESENT = shadersModPresent;
    }

    public static boolean shadersEnabled() {
        return IS_SHADERS_MOD_PRESENT && ShadersCompat.shaderPackLoaded();
    }

    public static void toggleLightMapShaders(boolean state) {
        ShadersCompat.toggleLightMap(state);
    }

    public static void optiFineSetActiveTexture(int texture) {
        ShadersCompat.setActiveTexture(texture);
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
