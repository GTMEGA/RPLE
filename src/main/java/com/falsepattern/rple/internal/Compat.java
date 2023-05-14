/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal;

import shadersmod.client.Shaders;

import cpw.mods.fml.client.FMLClientHandler;

public class Compat {
    public static boolean shadersEnabled() {
        return FMLClientHandler.instance().hasOptifine() && Shaders.shaderPackLoaded;
    }
}
