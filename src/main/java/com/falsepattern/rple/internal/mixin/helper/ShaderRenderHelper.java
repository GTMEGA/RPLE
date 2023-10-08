package com.falsepattern.rple.internal.mixin.helper;

import com.falsepattern.rple.api.client.RPLEShaderConstants;
import lombok.experimental.UtilityClass;

import static shadersmod.client.Shaders.setProgramUniform1i;

@UtilityClass
public final class ShaderRenderHelper {
    public static void enableTexturing() {
        setProgramUniform1i(RPLEShaderConstants.TEXTURING_ENABLED_ATTRIB_NAME, 1);
    }

    public static void disableTexturing() {
        setProgramUniform1i(RPLEShaderConstants.TEXTURING_ENABLED_ATTRIB_NAME, 0);
    }
}
