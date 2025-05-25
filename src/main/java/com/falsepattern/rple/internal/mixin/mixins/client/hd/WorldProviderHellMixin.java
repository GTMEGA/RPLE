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

import com.falsepattern.rple.internal.common.config.RPLEConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProviderHell;

@Mixin(WorldProviderHell.class)
public abstract class WorldProviderHellMixin {

    @WrapOperation(method = "getFogColor",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/util/Vec3;createVectorHelper(DDD)Lnet/minecraft/util/Vec3;"),
                   require = 1)
    private Vec3 hardcoreFogColor(double x, double y, double z, Operation<Vec3> original) {
        if (RPLEConfig.HD.MODE != RPLEConfig.HD.Mode.Disabled && RPLEConfig.HD.DARK_NETHER) {
            return original.call(0d, 0d, 0d);
        } else {
            return original.call(x, y, z);
        }
    }
}
