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

package com.falsepattern.rple.internal.mixin.mixins.common.rple;

import com.falsepattern.rple.api.common.block.RPLEBlockRenamed;
import com.falsepattern.rple.api.common.block.RPLEBlockRoot;
import com.falsepattern.rple.api.common.color.LightValueColor;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static com.falsepattern.rple.internal.mixin.Mixin.RPLE_ROOT_MIXIN_PRIORITY;

@Unique
@Mixin(value = Block.class, priority = RPLE_ROOT_MIXIN_PRIORITY)
@SuppressWarnings("unused")
public abstract class RPLEBlockRootImplMixin implements RPLEBlockRoot, RPLEBlockRenamed {
    @Override
    public short rple$getRawInternalColoredBrightness() {
        return packGreyscale(rple$renamed$getLightValue());
    }

    @Override
    public short rple$getRawInternalColoredBrightness(IBlockAccess world, int posX, int posY, int posZ) {
        return packGreyscale(rple$renamed$getLightValue(world, posX, posY, posZ));
    }

    @Override
    public short rple$getRawInternalColoredOpacity() {
        return packGreyscale(rple$renamed$getLightOpacity());
    }

    @Override
    public short rple$getRawInternalColoredOpacity(IBlockAccess world, int posX, int posY, int posZ) {
        return packGreyscale(rple$renamed$getLightOpacity(world, posX, posY, posZ));
    }

    @Override
    public short rple$getInternalColoredBrightness() {
        return LightValueColor.fromVanillaLightValue(rple$renamed$getLightValue()).rgb16();
    }

    @Override
    public short rple$getInternalColoredBrightness(IBlockAccess world, int posX, int posY, int posZ) {
        return LightValueColor.fromVanillaLightValue(rple$renamed$getLightValue(world, posX, posY, posZ)).rgb16();
    }

    @Override
    public short rple$getInternalColoredTranslucency() {
        return LightValueColor.fromVanillaLightOpacity(rple$renamed$getLightOpacity()).rgb16();
    }

    @Override
    public short rple$getInternalColoredTranslucency(IBlockAccess world, int posX, int posY, int posZ) {
        return LightValueColor.fromVanillaLightOpacity(rple$renamed$getLightOpacity(world, posX, posY, posZ)).rgb16();
    }

    private static short packGreyscale(int color) {
        color = color & 0xf;
        return (short) ((color << 8) | (color << 4) | color);
    }
}
