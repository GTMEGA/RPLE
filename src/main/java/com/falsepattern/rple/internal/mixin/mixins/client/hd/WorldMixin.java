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

package com.falsepattern.rple.internal.mixin.mixins.client.hd;

import com.falsepattern.rple.internal.HardcoreDarkness;
import com.falsepattern.rple.internal.common.config.RPLEConfig;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

@Mixin(World.class)
public abstract class WorldMixin {
    private static float value2;
    private static RPLEConfig.HD.Mode skyModeCache;

    @ModifyConstant(method = "getSunBrightnessBody",
                    constant = @Constant(floatValue = 0.8F),
                    remap = false,
                    require = 0)
    private float sky1(float constant) {
        if (!HardcoreDarkness.INSTANCE.isEnabled()) {
            return constant;
        }

        skyModeCache = RPLEConfig.HD.MODE;

        switch (skyModeCache) {
            case Both:
                return 1;
            case DynamicMoonlight:
                val lightList = RPLEConfig.HD.MOON_LIGHT_LIST;
                float moon = Minecraft.getMinecraft().theWorld.getCurrentMoonPhaseFactor();
                return 1 - (value2 = (float) lightList[(int) Math.round(moon / 0.25f)]);
            default:
                return constant;
        }
    }
    @ModifyConstant(method = "getSunBrightnessBody",
                    constant = @Constant(floatValue = 0.2F),
                    slice = @Slice(from = @At(value = "CONSTANT",
                                              args = "floatValue=0.8")),
                    remap = false,
                    require = 0)
    private float sky2(float constant) {
        if (!HardcoreDarkness.INSTANCE.isEnabled()) {
            return constant;
        }

        switch(skyModeCache) {
            case Both: return 0;
            case DynamicMoonlight: return value2;
            default: return constant;
        }
    }
}
