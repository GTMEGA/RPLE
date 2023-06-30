/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.block;

import com.falsepattern.rple.api.block.RPLEBlockColorizer;
import com.falsepattern.rple.api.color.RPLEColor;
import com.falsepattern.rple.api.color.RPLENamedColor;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class NullBlockColorizer implements RPLEBlockColorizer {
    private static final NullBlockColorizer INSTANCE = new NullBlockColorizer();

    public static RPLEBlockColorizer nullBlockColorizer() {
        return INSTANCE;
    }

    @Override
    public RPLEBlockColorizer brightness(int color) {
        return this;
    }

    @Override
    public RPLEBlockColorizer brightness(RPLEColor color) {
        return this;
    }

    @Override
    public RPLEBlockColorizer brightness(RPLENamedColor color) {
        return this;
    }

    @Override
    public RPLEBlockColorizer translucency(int color) {
        return this;
    }

    @Override
    public RPLEBlockColorizer translucency(RPLEColor color) {
        return this;
    }

    @Override
    public RPLEBlockColorizer translucency(RPLENamedColor color) {
        return this;
    }

    @Override
    public void apply() {
    }
}
