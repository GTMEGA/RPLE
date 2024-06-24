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

package com.falsepattern.rple.internal.client.lightmap;

import lombok.experimental.UtilityClass;
import org.lwjgl.opengl.GL13;

@UtilityClass
public final class LightMapConstants {
    public static final float LIGHT_MAP_COORDINATE_SCALE = Short.MAX_VALUE;
    public static final float LIGHT_MAP_BASE_COORDINATE_SCALE = 255F / Short.MAX_VALUE;
    public static final float LIGHT_MAP_TEXTURE_SCALE = 1F / (Short.MAX_VALUE - Short.MIN_VALUE);
    public static final float LIGHT_MAP_TEXTURE_TRANSLATION = Short.MAX_VALUE;

    public static final int LIGHT_MAP_1D_SIZE = 16;
    public static final int LIGHT_MAP_2D_SIZE = LIGHT_MAP_1D_SIZE * LIGHT_MAP_1D_SIZE;

    public static final int R_LIGHT_MAP_COLOR_BIT_MASK = 0xFF00FFFF;
    public static final int G_LIGHT_MAP_COLOR_BIT_MASK = 0xFFFF00FF;
    public static final int B_LIGHT_MAP_COLOR_BIT_MASK = 0xFFFFFF00;

    public static final int R_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING = GL13.GL_TEXTURE1;
    public static final int G_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING = GL13.GL_TEXTURE2;
    public static final int B_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING = GL13.GL_TEXTURE3;

    private static final int OPTIFINE_MAX_RESERVED_TEXTURE_SAMPLER = 40;
    public static final int R_LIGHT_MAP_SHADER_TEXTURE_SAMPLER = OPTIFINE_MAX_RESERVED_TEXTURE_SAMPLER + 1;
    public static final int G_LIGHT_MAP_SHADER_TEXTURE_SAMPLER = OPTIFINE_MAX_RESERVED_TEXTURE_SAMPLER + 2;
    public static final int B_LIGHT_MAP_SHADER_TEXTURE_SAMPLER = OPTIFINE_MAX_RESERVED_TEXTURE_SAMPLER + 3;

    public static final int R_LIGHT_MAP_SHADER_TEXTURE_SAMPLER_BINDING = GL13.GL_TEXTURE0 + R_LIGHT_MAP_SHADER_TEXTURE_SAMPLER;
    public static final int G_LIGHT_MAP_SHADER_TEXTURE_SAMPLER_BINDING = GL13.GL_TEXTURE0 + G_LIGHT_MAP_SHADER_TEXTURE_SAMPLER;
    public static final int B_LIGHT_MAP_SHADER_TEXTURE_SAMPLER_BINDING = GL13.GL_TEXTURE0 + B_LIGHT_MAP_SHADER_TEXTURE_SAMPLER;

    public static final int R_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING = GL13.GL_TEXTURE1;
    public static final int G_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING = GL13.GL_TEXTURE6;
    public static final int B_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING = GL13.GL_TEXTURE7;
}
