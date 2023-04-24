/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal;

public class Constants {
    public static final int BYTES_PER_VERTEX_VANILLA = 8;
    public static final int BYTES_PER_VERTEX_COLOR = BYTES_PER_VERTEX_VANILLA + 2 * 4;

    public static int extendBytesPerVertex(int vanilla) {
        return (vanilla / BYTES_PER_VERTEX_VANILLA) * BYTES_PER_VERTEX_COLOR;
    }
}
