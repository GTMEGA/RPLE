/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.client.render;

import net.minecraft.util.IIcon;

public class IIconWrapper implements IIcon {
    private final IIcon wrapped;
    public IIconWrapper(IIcon wrapped) {
        this.wrapped = wrapped;
    }
    @Override
    public int getIconWidth() {
        return wrapped.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return wrapped.getIconHeight();
    }

    @Override
    public float getMinU() {
        return wrapped.getMinU();
    }

    @Override
    public float getMaxU() {
        return wrapped.getMaxU();
    }

    @Override
    public float getInterpolatedU(double p_94214_1_) {
        return wrapped.getInterpolatedU(Math.max(Math.min(p_94214_1_, 16), 0));
    }

    @Override
    public float getMinV() {
        return wrapped.getMinV();
    }

    @Override
    public float getMaxV() {
        return wrapped.getMaxV();
    }

    @Override
    public float getInterpolatedV(double p_94207_1_) {
        return wrapped.getInterpolatedV(Math.max(Math.min(p_94207_1_, 16), 0));
    }

    @Override
    public String getIconName() {
        return wrapped.getIconName();
    }
}
