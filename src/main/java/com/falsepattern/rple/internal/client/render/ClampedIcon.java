/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.client.render;

import lombok.AllArgsConstructor;
import net.minecraft.util.IIcon;

@AllArgsConstructor
public final class ClampedIcon implements IIcon {
    private final IIcon delegate;

    @Override
    public int getIconWidth() {
        return delegate.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return delegate.getIconHeight();
    }

    @Override
    public float getMinU() {
        return delegate.getMinU();
    }

    @Override
    public float getMaxU() {
        return delegate.getMaxU();
    }

    @Override
    public float getMinV() {
        return delegate.getMinV();
    }

    @Override
    public float getMaxV() {
        return delegate.getMaxV();
    }

    @Override
    public float getInterpolatedU(double textureU) {
        return delegate.getInterpolatedU(clampTextureCoordinate(textureU));
    }

    @Override
    public float getInterpolatedV(double textureV) {
        return delegate.getInterpolatedV(clampTextureCoordinate(textureV));
    }

    private double clampTextureCoordinate(double textureCoordinate) {
        return Math.max(Math.min(textureCoordinate, 16), 0);
    }

    @Override
    public String getIconName() {
        return delegate.getIconName();
    }
}
