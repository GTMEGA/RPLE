/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal;

import cpw.mods.fml.client.FMLClientHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.*;
import shadersmod.client.Shaders;

/**
 * Non-minecraft stuff to avoid accidental classloading in spaghetti code
 */
public class Common {
    public static final Logger LOG = LogManager.getLogger(Tags.MODID);

    public static final int RED_LIGHT_MAP_TEXTURE_UNIT = GL13.GL_TEXTURE1;
    public static final int GREEN_LIGHT_MAP_TEXTURE_UNIT = GL13.GL_TEXTURE6;
    public static final int BLUE_LIGHT_MAP_TEXTURE_UNIT = GL13.GL_TEXTURE7;

    public static final int RED_LIGHT_MAP_TEXTURE_SAMPLER = GL13.GL_TEXTURE1;
    public static final int GREEN_LIGHT_MAP_TEXTURE_SAMPLER = GL13.GL_TEXTURE31 + 1;
    public static final int BLUE_LIGHT_MAP_TEXTURE_SAMPLER = GL13.GL_TEXTURE31 + 2;

    public static final String RED_LIGHT_MAP_UNIFORM_NAME = "redLightMap";
    public static final String GREEN_LIGHT_MAP_UNIFORM_NAME = "greenLightMap";
    public static final String BLUE_LIGHT_MAP_UNIFORM_NAME = "blueLightMap";

    static {
        System.out.println("OptiFine Shaders: " + (FMLClientHandler.instance().hasOptifine() && Shaders.shaderPackLoaded));
    }
}
