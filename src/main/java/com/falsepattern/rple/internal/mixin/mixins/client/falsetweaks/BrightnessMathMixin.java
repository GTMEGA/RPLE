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

package com.falsepattern.rple.internal.mixin.mixins.client.falsetweaks;

import com.falsepattern.falsetweaks.modules.ao.BrightnessMath;
import com.falsepattern.rple.api.client.ClientColorHelper;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = BrightnessMath.class,
       remap = false)
public abstract class BrightnessMathMixin {

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static int lerpBrightness(int a, int b, double fract) {
        return ClientColorHelper.cookieMixAOBrightness(a, b, 1 - fract, fract);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static int biLerpBrightness(int q00, int q10, int q01, int q11, double tx, double ty) {
        val xMin = 1 - tx;
        val xMax = tx;
        val yMin = 1 - ty;
        val yMax = ty;
        return ClientColorHelper.cookieMixAOBrightness(q00, q10, q01, q11, xMin * yMin, xMax * yMin, xMin * yMax, xMax * yMax);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static int averageBrightness(int a, int b) {
        return ClientColorHelper.cookieAverage(true, a, b);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static int averageBrightness(int a, int b, int c, int d) {
        return ClientColorHelper.cookieAverage(true, a, b, c, d);
    }
}
