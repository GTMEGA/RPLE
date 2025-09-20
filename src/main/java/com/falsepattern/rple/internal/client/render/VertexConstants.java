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

import com.falsepattern.falsetweaks.api.triangulator.VertexAPI;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public final class VertexConstants {
    /**
     * Overrides Vanilla value
     */
    @Getter
    private static int redIndexNoShader;
    /**
     * Grabbed from FalseTweaks
     */
    @Getter
    private static int greenIndexNoShader;
    /**
     * Grabbed from FalseTweaks
     */
    @Getter
    private static int blueIndexNoShader;

    /**
     * Overrides OptiFine value
     */
    @Getter
    private static int redIndexShader;
    /**
     * Grabbed from FalseTweaks
     */
    @Getter
    private static int greenIndexShader;
    /**
     * Grabbed from FalseTweaks
     */
    @Getter
    private static int blueIndexShader;

    public static void initVertexConstants() {
        val noShaderBuf = new int[2];
        val shaderBuf = new int[2];
        VertexAPI.allocateExtraVertexSlots(2, noShaderBuf, shaderBuf);
        redIndexNoShader = 7;
        greenIndexNoShader = noShaderBuf[0];
        blueIndexNoShader = noShaderBuf[1];
        redIndexShader = 6;
        greenIndexShader = shaderBuf[0];
        blueIndexShader = shaderBuf[1];
    }
}
