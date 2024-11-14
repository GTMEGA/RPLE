/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
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

    @Inject(method = "disableLightmap",
            at = @At("HEAD"),
            cancellable = true,
            require = 1)
    private void disableLightMaps(double p_78463_1_, CallbackInfo ci) {
        LightMap.lightMap().disable();
        ci.cancel();
    }

    @Inject(method = "enableLightmap",
            at = @At("HEAD"),
            cancellable = true,
            require = 1)
    private void enableLightMaps(double p_78463_1_, CallbackInfo ci) {
        LightMap.lightMap().enable();
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
