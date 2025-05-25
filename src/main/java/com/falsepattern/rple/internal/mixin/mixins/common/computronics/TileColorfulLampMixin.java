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

package com.falsepattern.rple.internal.mixin.mixins.common.computronics;

import com.falsepattern.rple.api.common.ServerColorHelper;
import com.falsepattern.rple.api.common.block.RPLECustomBlockBrightness;
import net.minecraft.world.IBlockAccess;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import static com.falsepattern.rple.api.common.color.LightValueColor.LIGHT_VALUE_0;

@Pseudo
@Mixin(targets = "pl.asie.computronics.tile.TileColorfulLamp", remap = false)
public abstract class TileColorfulLampMixin implements RPLECustomBlockBrightness {
    @Shadow
    public abstract int getLampColor();

    @Override
    public short rple$getCustomBrightnessColor() {
        return LIGHT_VALUE_0.rgb16();
    }

    @Override
    public short rple$getCustomBrightnessColor(int i) {
        return LIGHT_VALUE_0.rgb16();
    }

    @Override
    public short rple$getCustomBrightnessColor(@NotNull IBlockAccess access, int meta, int posX, int posY, int posZ) {
        short colour = (short) this.getLampColor();
        int red = ((colour >> 10) & 0x1f) >> 1;
        int green = ((colour >> 5) & 0x1f) >> 1;
        int blue = (colour & 0x1f) >> 1;
        return ServerColorHelper.RGB16FromRGBChannel4Bit(red, green, blue);
    }
}
