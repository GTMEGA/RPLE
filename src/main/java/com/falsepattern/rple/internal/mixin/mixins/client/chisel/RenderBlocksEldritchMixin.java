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

package com.falsepattern.rple.internal.mixin.mixins.client.chisel;

import com.falsepattern.rple.api.client.ClientColorHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.chisel.client.render.RenderBlocksEldritch;

@Mixin(RenderBlocksEldritch.class)
public abstract class RenderBlocksEldritchMixin extends RenderBlocks {
    @Shadow(remap = false)
    int[] L;

    @Inject(method = "setupSides",
            at = @At("RETURN"),
            remap = false,
            require = 1)
    private void colouredSidesAverageBrightness(IIcon icon,
                                                int a,
                                                int b,
                                                int c,
                                                int d,
                                                int e,
                                                int ta,
                                                int tb,
                                                int tc,
                                                int td,
                                                CallbackInfo ci) {
        L[e] = ClientColorHelper.cookieAverage(false,
                                    brightnessBottomLeft,
                                    brightnessTopLeft,
                                    brightnessTopRight,
                                    brightnessBottomRight);
    }
}
