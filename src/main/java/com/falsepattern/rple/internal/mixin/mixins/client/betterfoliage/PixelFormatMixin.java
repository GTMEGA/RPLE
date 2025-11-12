/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
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

package com.falsepattern.rple.internal.mixin.mixins.client.betterfoliage;

import com.falsepattern.rple.api.client.ClientColorHelper;
import com.falsepattern.rple.api.client.CookieMonster;
import lombok.val;
import mods.octarinecore.client.render.PixelFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(PixelFormat.class)
public abstract class PixelFormatMixin {

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite(remap = false)
    public static final int brMul(int $receiver, float f) {
        return ClientColorHelper.cookieMul($receiver, f);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @SuppressWarnings("OverwriteModifiers") // wants to be transient?
    @Overwrite(remap = false)
    public static final int brSum(Float multiplier, int[] brightness) {
        long tmp;
        val len = brightness.length;
        switch (len) {
            case 0:
                tmp = 0;
                break;
            case 1:
                tmp = CookieMonster.RGB64FromCookie(brightness[0]);
                break;
            case 2:
                tmp = ClientColorHelper.RGB64Average(CookieMonster.RGB64FromCookie(brightness[0]),
                                                     CookieMonster.RGB64FromCookie(brightness[1]),
                                                     false);
                break;
            case 4:
                tmp = ClientColorHelper.RGB64Average(CookieMonster.RGB64FromCookie(brightness[0]),
                                                     CookieMonster.RGB64FromCookie(brightness[1]),
                                                     CookieMonster.RGB64FromCookie(brightness[2]),
                                                     CookieMonster.RGB64FromCookie(brightness[3]),
                                                     false);
                break;
            default:
                val rgb64Bright = new long[len];
                for (int i = 0; i < len; i++) {
                    rgb64Bright[i] = CookieMonster.RGB64FromCookie(brightness[i]);
                }
                tmp = ClientColorHelper.RGB64Average(rgb64Bright, len, false);
        }
        if (multiplier != null) {
            tmp = ClientColorHelper.RGB64Mul(tmp, multiplier);
        }
        return CookieMonster.cookieFromRGB64(tmp);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite(remap = false)
    public static final int brWeighted(int br1, float weight1, int br2, float weight2) {
        return ClientColorHelper.cookieMixAOBrightness(br1, br2, weight1, weight2);
    }
}
