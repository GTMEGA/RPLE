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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

// TODO: Suspected reason that CCL-rendered items have broken colors.
@Mixin(value = CCRenderState.class, remap = false)
public abstract class CCRenderStateMixin {

    @Shadow
    public static void setBrightness(int brightness) {}

    @Shadow
    public static int brightness() {
        return 0;
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static void pullLightmap() {
        setBrightness(CookieMonster.cookieFromRGB64(ExtendedOpenGlHelper.lastPackedBrightness()));
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static void pushLightmap() {
        ExtendedOpenGlHelper.setLightMapTextureCoordsPacked(CookieMonster.RGB64FromCookie(brightness()));
    }
}
