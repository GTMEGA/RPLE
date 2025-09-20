package com.falsepattern.rple.internal.mixin.mixins.client.swansong;

import com.falsepattern.rple.internal.client.render.VertexConstants;
import com.falsepattern.rple.internal.mixin.interfaces.swansong.RPLEShaderVert;
import com.ventooth.swansong.tessellator.ShaderVert;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ShaderVert.class, remap = false)
public abstract class ShaderVertMixin implements RPLEShaderVert {
    @Shadow
    public int lightMapUV;

    @Unique
    public int rple$lightMapUVGreen;
    @Unique
    public int rple$lightMapUVBlue;

    @Inject(method = "toIntArray",
            at = @At("HEAD"),
            require = 1)
    private void addRGBLightMapValues(int index, int[] output, CallbackInfo ci) {
        output[VertexConstants.getGreenIndexShader() + index] = rple$lightMapUVGreen;
        output[VertexConstants.getBlueIndexShader() + index] = rple$lightMapUVBlue;
    }

    @Override
    public void rple$lightMapUVRed(int value) {
        this.lightMapUV = value;
    }

    @Override
    public void rple$lightMapUVGreen(int value) {
        rple$lightMapUVGreen = value;
    }

    @Override
    public void rple$lightMapUVBlue(int value) {
        rple$lightMapUVBlue = value;
    }
}
