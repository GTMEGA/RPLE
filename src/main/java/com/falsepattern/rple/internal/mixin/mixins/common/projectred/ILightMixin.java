/*
 * Copyright (c) 2023 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.projectred;

import com.falsepattern.rple.api.block.RPLEBlockBrightnessColorProvider;
import com.falsepattern.rple.api.color.RPLEColor;
import com.falsepattern.rple.internal.mixin.helper.ProjectRedHelper;
import mrtjp.projectred.illumination.ILight;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Unique
@Mixin(ILight.class)
public interface ILightMixin extends RPLEBlockBrightnessColorProvider {
    @Override
    default @NotNull RPLEColor rple$getCustomBrightnessColor() {
        return ProjectRedHelper.getLightBrightnessColor((ILight) this);
    }

    @Override
    default @NotNull RPLEColor rple$getCustomBrightnessColor(int blockMeta) {
        return ProjectRedHelper.getLightBrightnessColor((ILight) this);
    }

    @Override
    default @NotNull RPLEColor rple$getCustomBrightnessColor(@NotNull IBlockAccess world,
                                                             int blockMeta,
                                                             int posX,
                                                             int posY,
                                                             int posZ) {
        return ProjectRedHelper.getLightBrightnessColor((ILight) this);
    }
}
