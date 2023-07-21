/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
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
