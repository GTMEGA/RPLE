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

package com.falsepattern.rple.internal.mixin.mixins.client.dsurround;

import com.falsepattern.rple.api.client.ClientColorHelper;
import com.falsepattern.rple.api.client.CookieMonster;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.val;
import org.blockartistry.mod.DynSurround.client.weather.StormRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;

@Mixin(StormRenderer.class)
public abstract class StormRendererMixin {
    @WrapOperation(method = "render",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/renderer/Tessellator;setBrightness(I)V",
                            ordinal = 1),
                   require = 1)
    private void suppressSetBrightness(Tessellator instance, int light, Operation<Void> original) {

    }
    @WrapOperation(method = "render",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/multiplayer/WorldClient;getLightBrightnessForSkyBlocks(IIII)I",
                            ordinal = 1),
                   require = 1)
    private int fixSnow1(WorldClient instance, int x, int y, int z, int min, Operation<Integer> original, @Local
    Tessellator tessellator) {
        val brightness = original.call(instance, x, y, z, min);
        val shifted = CookieMonster.cookieFromRGB64(ClientColorHelper.RGB64ForEach(CookieMonster.RGB64FromCookie(brightness), c -> (c * 3 + 0xf0) / 4));
        tessellator.setBrightness(shifted);
        return 0;
    }
}
