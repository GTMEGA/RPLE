/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api;

import static com.falsepattern.rple.internal.Tags.*;

@SuppressWarnings("unused")
public final class RPLEAPI {
    public static final String RPLE_MOD_ID = MOD_ID;
    public static final String RPLE_MOD_NAME = MOD_NAME;
    public static final String RPLE_VERSION = VERSION;

    private RPLEAPI() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
