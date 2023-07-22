/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.client;

public final class RPLEShaderConstants {
    public static final String RED_LIGHT_MAP_UNIFORM_NAME = "redLightMap";
    public static final String GREEN_LIGHT_MAP_UNIFORM_NAME = "greenLightMap";
    public static final String BLUE_LIGHT_MAP_UNIFORM_NAME = "blueLightMap";

    public static final String EDGE_TEX_COORD_ATTRIB_NAME = "rple_edgeTexCoord";
    public static final int edgeTexCoordAttrib = 13;
    public static boolean useRPLEEdgeTexCoordAttrib = false;
    public static boolean progUseRPLEEdgeTexCoordAttrib = false;
}
