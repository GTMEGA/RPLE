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

package com.falsepattern.rple.internal.mixin.mixins.client.projectred;

import codechicken.lib.render.CCRenderState;
import com.falsepattern.rple.api.client.ClientColorHelper;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.mixin.extension.ExtendedOpenGlHelper;
import lombok.val;
import mrtjp.projectred.core.RenderHalo$;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderHalo$.class, remap = false)
public abstract class RenderHaloMixin {
    private static ResourceLocation glowTex;
    private int oldTexture;

    @Inject(method = "prepareRenderState()V",
            at = @At("HEAD"),
            require = 1)
    private void prepareFixColor(CallbackInfo ci) {
        if (glowTex == null)
            glowTex = new ResourceLocation(Tags.MOD_ID, "textures/blocks/glow_solid.png");

        val brightness = ClientColorHelper.vanillaFromBlockSky4Bit(15, 15);
        val rgb64 = ClientColorHelper.RGB64FromVanillaMonochrome(brightness);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glColor4f(1, 1, 1, 1);

        ExtendedOpenGlHelper.setLightMapTextureCoordsRGB64(rgb64);
        oldTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        CCRenderState.changeTexture(glowTex);
    }

    @Inject(method = "restoreRenderState()V",
            at = @At("RETURN"),
            require = 1)
    private void restoreFixColor(CallbackInfo ci) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, oldTexture);
        GL11.glPopAttrib();
    }
}
