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
