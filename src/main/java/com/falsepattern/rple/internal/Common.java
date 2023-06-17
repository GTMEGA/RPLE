/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.*;

/**
 * Non-minecraft stuff to avoid accidental classloading in spaghetti code
 */
public class Common {
    public static final Logger LOG = LogManager.getLogger(Tags.MODID);

    public static final int LIGHT_MAP_1D_SIZE = 16;
    public static final int LIGHT_MAP_2D_SIZE = LIGHT_MAP_1D_SIZE * LIGHT_MAP_1D_SIZE;

    public static final int RED_LIGHT_MAP_SHADER_TEXTURE_SAMPLER = 31 + 10;
    public static final int GREEN_LIGHT_MAP_SHADER_TEXTURE_SAMPLER = 32 + 10;
    public static final int BLUE_LIGHT_MAP_SHADER_TEXTURE_SAMPLER = 33 + 10;

    public static int RED_LIGHT_MAP_TEXTURE_UNIT = GL13.GL_TEXTURE1;
    public static final int GREEN_LIGHT_MAP_TEXTURE_UNIT = GL13.GL_TEXTURE6;
    public static final int BLUE_LIGHT_MAP_TEXTURE_UNIT = GL13.GL_TEXTURE7;

    public static final int RED_LIGHT_MAP_SHADER_TEXTURE_UNIT = GL13.GL_TEXTURE0 + RED_LIGHT_MAP_SHADER_TEXTURE_SAMPLER;
    public static final int GREEN_LIGHT_MAP_SHADER_TEXTURE_UNIT = GL13.GL_TEXTURE0 + GREEN_LIGHT_MAP_SHADER_TEXTURE_SAMPLER;
    public static final int BLUE_LIGHT_MAP_SHADER_TEXTURE_UNIT = GL13.GL_TEXTURE0 + BLUE_LIGHT_MAP_SHADER_TEXTURE_SAMPLER;

    public static final String RED_LIGHT_MAP_UNIFORM_NAME = "redLightMap";
    public static final String GREEN_LIGHT_MAP_UNIFORM_NAME = "greenLightMap";
    public static final String BLUE_LIGHT_MAP_UNIFORM_NAME = "blueLightMap";
}
