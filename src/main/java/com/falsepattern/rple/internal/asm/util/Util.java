/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.asm.util;

import lombok.val;
import org.apache.commons.io.IOUtils;

import net.minecraft.launchwrapper.Launch;

import java.io.IOException;

public class Util {
    public static byte[] bytesFromInternalName(String internalName) {
        val classLoader = Launch.classLoader;
        val in = classLoader.getResourceAsStream(internalName + ".class");
        if (in == null) {
            return null;
        }
        try {
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

