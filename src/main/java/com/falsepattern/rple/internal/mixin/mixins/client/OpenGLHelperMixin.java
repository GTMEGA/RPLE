package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.LightMap;
import com.falsepattern.rple.internal.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.OpenGlHelper;

import static com.falsepattern.rple.internal.Utils.MASK;

@Mixin(OpenGlHelper.class)
public abstract class OpenGLHelperMixin {
    @Shadow public static int lightmapTexUnit;

    @Shadow
    public static void setLightmapTextureCoords(int p_77475_0_, float p_77475_1_, float p_77475_2_) {
    }

    @Inject(method = "setLightmapTextureCoords",
            at = @At(value = "HEAD"),
            cancellable = true,
            require = 1)
    private static void onSet(int texture, float u, float v, CallbackInfo ci) {
        int value = (int)(u) | ((int)(v) << 16);
        if ((value & MASK) != 0) {
            setLightmapTextureCoords(LightMap.textureUnitRed, Utils.extractFloat(value, 20), Utils.extractFloat(value, 25));
            setLightmapTextureCoords(LightMap.textureUnitGreen, Utils.extractFloat(value, 10), Utils.extractFloat(value, 15));
            setLightmapTextureCoords(LightMap.textureUnitBlue, Utils.extractFloat(value, 0), Utils.extractFloat(value, 5));
            ci.cancel();
        } else {
            if (texture == lightmapTexUnit) {
                if (lightmapTexUnit != LightMap.textureUnitRed) {
                    setLightmapTextureCoords(LightMap.textureUnitRed, u, v);
                }
                setLightmapTextureCoords(LightMap.textureUnitGreen, u, v);
                setLightmapTextureCoords(LightMap.textureUnitBlue, u, v);
            }
        }
    }
}
