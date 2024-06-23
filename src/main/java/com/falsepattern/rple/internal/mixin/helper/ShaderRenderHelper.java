package com.falsepattern.rple.internal.mixin.helper;

import com.falsepattern.rple.internal.client.render.ShaderConstants;
import lombok.experimental.UtilityClass;

import static shadersmod.client.Shaders.setProgramUniform1i;

@UtilityClass
public final class ShaderRenderHelper {
    public static void enableTexturing() {
        setProgramUniform1i(ShaderConstants.TEXTURING_ENABLED_ATTRIB_NAME, 1);
    }

    public static void disableTexturing() {
        setProgramUniform1i(ShaderConstants.TEXTURING_ENABLED_ATTRIB_NAME, 0);
    }
}
