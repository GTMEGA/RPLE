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

package com.falsepattern.rple.internal;

import com.falsepattern.falsetweaks.api.ThreadedChunkUpdates;
import com.ventooth.swansong.pbr.PBRTextureEngine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.experimental.UtilityClass;
import lombok.var;
import makamys.neodymium.Neodymium;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.launchwrapper.Launch;

import java.io.IOException;

@UtilityClass
public final class Compat {
    private static final boolean IS_SWANSONG_PRESENT;
    private static final boolean IS_FALSETWEAKS_PRESENT;
    private static Boolean NEODYMIUM = null;

    static {
        var swanSongPresent = false;
        try {
            swanSongPresent = Launch.classLoader.getClassBytes("com.ventooth.swansong.SwanSong") != null;
        } catch (IOException ignored) {
        }
        IS_SWANSONG_PRESENT = swanSongPresent;
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

    public static boolean neodymiumInstalled() {
        if (NEODYMIUM != null) {
            return NEODYMIUM;
        }
        try {
            NEODYMIUM = Launch.classLoader.getClassBytes("makamys.neodymium.Neodymium") != null;
        } catch (IOException e) {
            e.printStackTrace();
            NEODYMIUM = false;
        }
        return NEODYMIUM;
    }

    public static boolean neodymiumActive() {
        return neodymiumInstalled() && Neodymium.isActive();
    }

    @SideOnly(Side.CLIENT)
    public static boolean shadersEnabled() {
        return IS_SWANSONG_PRESENT;
    }

    @SideOnly(Side.CLIENT)
    public static void optiFineSetActiveTexture(int texture) {
        SwanSongCompat.isDefaultTexUnit(OpenGlHelper.defaultTexUnit == texture);
    }

    @SideOnly(Side.CLIENT)
    public static Tessellator tessellator() {
        if (ThreadedChunkUpdates.isEnabled()) {
            return ThreadedChunkUpdates.getThreadTessellator();
        }
        return Tessellator.instance;
    }

    private static class SwanSongCompat {
        public static void isDefaultTexUnit(boolean toggle) {
            PBRTextureEngine.isDefaultTexUnit(toggle);
        }
    }
}
