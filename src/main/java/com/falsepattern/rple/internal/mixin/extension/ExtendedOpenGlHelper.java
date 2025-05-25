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

package com.falsepattern.rple.internal.mixin.extension;

import com.falsepattern.rple.api.client.ClientColorHelper;
import com.falsepattern.rple.internal.Compat;
import com.falsepattern.rple.internal.client.lightmap.LightMapConstants;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.client.renderer.OpenGlHelper;

@UtilityClass
// TODO: [ENTITY] Expose this for users to set colors when rendering entities etc
public final class ExtendedOpenGlHelper {
    private static long LAST_RGB64 = 0;

    public static boolean BYPASS = false;

    public static void setLightMapTextureCoordsRGB64(long rgb64) {
        BYPASS = true;
        try {
            LAST_RGB64 = rgb64;

            val redBrightness = ClientColorHelper.vanillaFromRGB64Red(rgb64);
            val greenBrightness = ClientColorHelper.vanillaFromRGB64Green(rgb64);
            val blueBrightness = ClientColorHelper.vanillaFromRGB64Blue(rgb64);

            if (Compat.shadersEnabled()) {
                OpenGlHelper.setLightmapTextureCoords(LightMapConstants.R_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING, redBrightness & 0xFFFF, redBrightness >>> 16);
                OpenGlHelper.setLightmapTextureCoords(LightMapConstants.G_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING, greenBrightness & 0xFFFF, greenBrightness >>> 16);
                OpenGlHelper.setLightmapTextureCoords(LightMapConstants.B_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING, blueBrightness & 0xFFFF, blueBrightness >>> 16);
            } else {
                OpenGlHelper.setLightmapTextureCoords(LightMapConstants.R_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING, redBrightness & 0xFFFF, redBrightness >>> 16);
                OpenGlHelper.setLightmapTextureCoords(LightMapConstants.G_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING, greenBrightness & 0xFFFF, greenBrightness >>> 16);
                OpenGlHelper.setLightmapTextureCoords(LightMapConstants.B_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING, blueBrightness & 0xFFFF, blueBrightness >>> 16);
            }
        } finally {
            BYPASS = false;
        }
    }

    public static long lastRGB64() {
        return LAST_RGB64;
    }
}
