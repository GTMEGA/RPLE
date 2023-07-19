/*
 * Copyright (c) 2023 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
    private static int redIndexNoShader = 7;
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
    @Getter
    private static int rpleEdgeTexUIndexNoShader;
    @Getter
    private static int rpleEdgeTexVIndexNoShader;

    /**
     * Overrides OptiFine value
     */
    @Getter
    private static int redIndexShader = 6;
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
    @Getter
    private static int rpleEdgeTexUIndexShader;
    @Getter
    private static int rpleEdgeTexVIndexShader;

    public static void initVertexConstants() {
        val noShaderBuf = new int[4];
        val shaderBuf = new int[4];
        VertexAPI.allocateExtraVertexSlots(4, noShaderBuf, shaderBuf);
        redIndexNoShader = 7;
        greenIndexNoShader = noShaderBuf[0];
        blueIndexNoShader = noShaderBuf[1];
        rpleEdgeTexUIndexNoShader = noShaderBuf[2];
        rpleEdgeTexVIndexNoShader = noShaderBuf[3];
        redIndexShader = 6;
        greenIndexShader = shaderBuf[0];
        blueIndexShader = shaderBuf[1];
        rpleEdgeTexUIndexShader = shaderBuf[2];
        rpleEdgeTexVIndexShader = shaderBuf[3];
    }
}
