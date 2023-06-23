/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.block;

import lombok.RequiredArgsConstructor;

@Deprecated
@RequiredArgsConstructor
public enum Lamps {
    RED(15, 0, 0),
    MAGENTA(15, 0, 15),
    PURPLE(7, 0, 15),
    BLUE(0, 0, 15),
    AZURE(7, 7, 15),
    CYAN(0, 15, 15),
    GREEN(0, 15, 0),
    LIME(7, 15, 7),
    YELLOW(15, 15, 0),
    ORANGE(15, 7, 0),
    PINK(15, 7, 15),
    WHITE(15, 15, 15);

    public final int r;
    public final int g;
    public final int b;
}
