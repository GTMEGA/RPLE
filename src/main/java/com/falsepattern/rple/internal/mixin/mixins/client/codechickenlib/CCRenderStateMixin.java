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

package com.falsepattern.rple.internal.mixin.mixins.client.codechickenlib;

import codechicken.lib.render.CCRenderState;
import com.falsepattern.rple.api.client.CookieMonster;
import com.falsepattern.rple.internal.mixin.extension.ExtendedOpenGlHelper;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: Suspected reason that CCL-rendered items have broken colors.
@Mixin(value = CCRenderState.class, remap = false)
public abstract class CCRenderStateMixin {
    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static void pullLightmap() {
        stubpackage.codechicken.lib.render.CCRenderState.setBrightness(CookieMonster.cookieFromRGB64(ExtendedOpenGlHelper.lastRGB64()));
    }

    @Dynamic
    @Inject(method = "pullLightmapInstance",
            at = @At("HEAD"),
            cancellable = true,
            require = 0,
            expect = 0)
    private void hijackPullLightmapInstance(CallbackInfo ci) {
        //noinspection DataFlowIssue
        ((nhstubs.stubpackage.codechicken.lib.render.CCRenderState)(Object)this).setBrightnessInstance(CookieMonster.cookieFromRGB64(ExtendedOpenGlHelper.lastRGB64()));
        ci.cancel();
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static void pushLightmap() {
        ExtendedOpenGlHelper.setLightMapTextureCoordsRGB64(CookieMonster.RGB64FromCookie(stubpackage.codechicken.lib.render.CCRenderState.brightness));
    }

    @Dynamic
    @Inject(method = "pushLightmapInstance",
            at = @At("HEAD"),
            cancellable = true,
            require = 0,
            expect = 0)
    private void hijackPushLightmapInstance(CallbackInfo ci) {
        //noinspection DataFlowIssue
        ExtendedOpenGlHelper.setLightMapTextureCoordsRGB64(CookieMonster.RGB64FromCookie(((nhstubs.stubpackage.codechicken.lib.render.CCRenderState)(Object)this).brightness));
        ci.cancel();
    }
}
