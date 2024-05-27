/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.client.lightmap.LightMap;
import com.falsepattern.rple.internal.client.render.LightValueOverlayRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.falsepattern.rple.internal.common.config.RPLEConfig.Debug.RGB_LIGHT_OVERLAY;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin implements IResourceManagerReloadListener {
    @Final
    @Shadow
    private DynamicTexture lightmapTexture;
    @Final
    @Shadow
    private ResourceLocation locationLightMap;
    @Final
    @Shadow
    private int[] lightmapColors;

    @Shadow
    private boolean lightmapUpdateNeeded;

    @Inject(method = "<init>",
            at = @At("RETURN"),
            require = 1)
    private void setupColorLightMaps(Minecraft minecraft, IResourceManager resourceManager, CallbackInfo ci) {
        LightMap.lightMap().generateTextures();
    }

    @Inject(method = "enableLightmap",
            at = @At("HEAD"),
            cancellable = true,
            require = 1)
    private void enableLightMaps(double p_78463_1_, CallbackInfo ci) {
        LightMap.lightMap().prepare();
        ci.cancel();
    }

    @Inject(method = "updateLightmap",
            at = @At("HEAD"),
            cancellable = true,
            require = 1)
    private void updateLightMap(float partialTickTime, CallbackInfo ci) {
        LightMap.lightMap().update(partialTickTime);
        lightmapUpdateNeeded = false;
        // TODO: This is not compatible with the OptiFine `CustomColorizer`
        ci.cancel();
    }

    @Inject(method = "renderWorld",
            at = @At(value = "CONSTANT",
                     args = "stringValue=destroyProgress",
                     shift = At.Shift.BY,
                     by = -3),
            require = 1)
    private void renderLightValueOverlay(float partialTickTime, long expectedFrameDoneTimeNs, CallbackInfo ci) {
        if (RGB_LIGHT_OVERLAY)
            LightValueOverlayRenderer.renderLightValueOverlay();
    }
}
