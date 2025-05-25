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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.WorldProvider;

@Mixin(WorldProvider.class)
public abstract class WorldProviderMixin {
    @ModifyConstant(method = "getFogColor",
                    constant = {
                            @Constant(floatValue = 0.94f),
                            @Constant(floatValue = 0.91f),
                    },
                    require = 0)
    private float hardcoreFog1(float constant) {
        if (HardcoreDarkness.INSTANCE.isEnabled()) {
            return 1;
        }
        return constant;
    }

    @ModifyConstant(method = "getFogColor",
                    constant = {
                            @Constant(floatValue = 0.06f),
                            @Constant(floatValue = 0.09f),
                    },
                    require = 0)
    private float hardcoreFog2(float constant) {
        if (HardcoreDarkness.INSTANCE.isEnabled()) {
            return 0;
        }
        return constant;
    }
}
