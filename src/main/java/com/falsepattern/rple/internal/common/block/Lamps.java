/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.block;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Lamps {
    BLACK     (3 , 0 , 3 ), // Why do we have this? It's just black. | Step aside; this is just an implementation issue.
    DARK_GRAY     (5 , 5 , 5 ),
    GRAY      (8 , 8 , 8 ),
    LIGHT_GRAY(11 , 11 , 11 ),
    WHITE     (15, 15, 15),
    DARK_RED     (11, 0, 0),
    RED       (15, 0 , 0 ),
    MAGENTA   (14, 0 , 14),
    BLUE_PURPLE   (5, 0 , 10),
    BLUE_GRAY   (7, 7 , 11),
    DARK_BLUE   (0, 0 , 11),
    BLUE      (0 , 0 , 15),
    LIGHT_BLUE(7 , 7 , 15),
    CYAN      (0 , 15, 14),
    PALE_BLUE(12 , 12 , 14),
    PINK      (9, 4 , 9),
    BRIGHT_PINK     (15 , 8, 15),
    PURPLE    (8 , 0 , 15),
    DARK_PURPLE    (5 , 0 , 8),
    SEA_GREEN    (10 , 14 , 11),
    GREEN     (0 , 14, 0 ),
    LIME     (2 , 15, 0 ),
    YELLOW    (14, 14, 0 ),
    DARK_YELLOW    (9, 9, 0 ),
    BROWN     (8 , 4 , 0 ),
    ORANGE    (13, 6 , 0 ),
    BRIGHT_ORANGE    (14, 9 , 1 ),
    RED_ORANGE    (15, 6 , 0 ),
    PALE_GREEN    (12, 14 , 12 ),
    LIGHT_GREEN    (9, 14 , 9 ),
    YELLOW_GREEN    (10, 12 , 1 ),
    LIGHT_PURPLE    (12, 8 , 15 );

    public final int r;
    public final int g;
    public final int b;
}
