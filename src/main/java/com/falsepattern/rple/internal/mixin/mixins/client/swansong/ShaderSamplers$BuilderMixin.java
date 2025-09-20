package com.falsepattern.rple.internal.mixin.mixins.client.swansong;

import com.falsepattern.rple.internal.client.lightmap.LightMapConstants;
import com.falsepattern.rple.internal.client.render.ShaderConstants;
import com.ventooth.swansong.shader.uniform.Uniform;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(targets = "com.ventooth.swansong.shader.ShaderSamplers$SamplerUniformListBuilder", remap = false)
public abstract class ShaderSamplers$BuilderMixin {
    @Shadow
    @Final
    private List<Uniform<?>> uniforms;

    @Inject(method = "add",
            at = @At("HEAD"),
            require = 1)
    private void addColoredLightmaps(String name, Uniform.IntSupplier src, CallbackInfoReturnable<Object> cir) {
        if ("lightmap".equals(name)) {
            uniforms.add(new Uniform.OfInt(ShaderConstants.RED_LIGHT_MAP_UNIFORM_NAME,
                                           () -> LightMapConstants.R_LIGHT_MAP_SHADER_TEXTURE_SAMPLER,
                                           Uniform::set));
            uniforms.add(new Uniform.OfInt(ShaderConstants.GREEN_LIGHT_MAP_UNIFORM_NAME,
                                           () -> LightMapConstants.G_LIGHT_MAP_SHADER_TEXTURE_SAMPLER,
                                           Uniform::set));
            uniforms.add(new Uniform.OfInt(ShaderConstants.BLUE_LIGHT_MAP_UNIFORM_NAME,
                                           () -> LightMapConstants.B_LIGHT_MAP_SHADER_TEXTURE_SAMPLER,
                                           Uniform::set));
        }
    }
}
