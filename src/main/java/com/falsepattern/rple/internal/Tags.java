/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal;

public class Tags {
    public static final String MOD_ID = "GRADLETOKEN_MODID";
    public static final String MOD_NAME = "GRADLETOKEN_MODNAME";
    public static final String VERSION = "GRADLETOKEN_VERSION";
    public static final String GROUP_NAME = "GRADLETOKEN_GROUPNAME";

    public static final String MINECRAFT_VERSION = "[1.7.10]";
    public static final String DEPENDENCIES = "required-after:falsepatternlib@[0.11,);" +
                                              "required-after:chunkapi@[0.2,);" +
                                              "required-after:lumina;" +
                                              "required-after:falsetweaks@[2.4,)";

    public static final String INTERNAL_GROUP_NAME = GROUP_NAME + ".internal";
    public static final String API_GROUP_NAME = GROUP_NAME + ".api";

    public static final String PROXY_GROUP_NAME = INTERNAL_GROUP_NAME + ".proxy";
    public static final String CLIENT_PROXY_CLASS_NAME = PROXY_GROUP_NAME + ".ClientProxy";
    public static final String SERVER_PROXY_CLASS_NAME = PROXY_GROUP_NAME + ".ServerProxy";
}
