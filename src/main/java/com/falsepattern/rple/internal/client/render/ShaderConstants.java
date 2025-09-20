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

package com.falsepattern.rple.internal.client.render;

public final class ShaderConstants {
    public static final String RED_LIGHT_MAP_UNIFORM_NAME = "redLightMap";
    public static final String GREEN_LIGHT_MAP_UNIFORM_NAME = "greenLightMap";
    public static final String BLUE_LIGHT_MAP_UNIFORM_NAME = "blueLightMap";

    public static final String TEXTURING_ENABLED_ATTRIB_NAME = "rple_texturingEnabled";

    public static final int TEX_0_UNIFORM_VALUE = 0;
    public static final int TEX_1_UNIFORM_VALUE = 2;
    public static final int TEX_2_UNIFORM_VALUE = 3;

    public static final String TEX_0_UNIFORM_NAME = "albedoTex";
    public static final String TEX_1_UNIFORM_NAME = "normalTex";
    public static final String TEX_2_UNIFORM_NAME = "specularTex";
}
