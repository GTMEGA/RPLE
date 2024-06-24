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

package com.falsepattern.rple.internal.mixin.mixins.client.optifine;

import net.minecraft.client.renderer.OpenGlHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stubpackage.GlStateManager;

@Mixin(OpenGlHelper.class)
public abstract class OpenGLHelperMixin {
    @Inject(method = "initializeTextures",
            at = @At("RETURN"),
            require = 1)
    private static void initGlStateManager(CallbackInfo ci) {
        GlStateManager.GL_FRAMEBUFFER = OpenGlHelper.field_153198_e;
        GlStateManager.GL_RENDERBUFFER = OpenGlHelper.field_153199_f;
        GlStateManager.GL_COLOR_ATTACHMENT0 = OpenGlHelper.field_153200_g;
        GlStateManager.GL_DEPTH_ATTACHMENT = OpenGlHelper.field_153201_h;
    }
}
