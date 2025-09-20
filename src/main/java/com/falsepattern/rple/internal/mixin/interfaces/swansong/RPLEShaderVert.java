package com.falsepattern.rple.internal.mixin.interfaces.swansong;

import com.ventooth.swansong.tessellator.ShaderVert;

public interface RPLEShaderVert {
    @SuppressWarnings("CastToIncompatibleInterface")
    static RPLEShaderVert of(ShaderVert thiz) {
        return (RPLEShaderVert) thiz;
    }

    void rple$lightMapUVRed(int value);

    void rple$lightMapUVGreen(int value);

    void rple$lightMapUVBlue(int value);
}
