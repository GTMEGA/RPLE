/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package com.falsepattern.rple.internal.mixin.mixins.common.appliedenergistics2;

import appeng.block.AEBaseTileBlock;
import appeng.block.networking.BlockCableBus;
import appeng.parts.ICableBusContainer;
import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.block.RPLECustomBlockBrightness;
import com.falsepattern.rple.api.common.color.LightValueColor;
import com.falsepattern.rple.internal.mixin.interfaces.appliedenergistics2.ICableBusContainerMixin;
import lombok.val;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static com.falsepattern.rple.api.common.color.LightValueColor.LIGHT_VALUE_0;

@Mixin(value = BlockCableBus.class,
       remap = false)
public abstract class BlockCableBusMixin extends AEBaseTileBlock implements RPLECustomBlockBrightness {
    public BlockCableBusMixin(Material mat) {
        super(mat);
    }

    @Shadow
    protected abstract ICableBusContainer cb(IBlockAccess world, int posX, int posY, int posZ);

    @Override
    public short rple$getCustomBrightnessColor() {
        return LIGHT_VALUE_0.rgb16();
    }

    @Override
    public short rple$getCustomBrightnessColor(int blockMeta) {
        return LIGHT_VALUE_0.rgb16();
    }

    @Override
    public short rple$getCustomBrightnessColor(@NotNull IBlockAccess world,
                                                            int blockMeta,
                                                            int posX,
                                                            int posY,
                                                            int posZ) {
        val otherBlock = world.getBlock(posX, posY, posZ);
        if (otherBlock != this) {
            val otherBlockMeta = world.getBlockMetadata(posX, posY, posZ);
            return RPLEBlock.of(otherBlock).rple$getBrightnessColor(world, otherBlockMeta, posX, posY, posZ);
        }
        val cb = cb(world, posX, posY, posZ);
        if (cb instanceof ICableBusContainerMixin)
            return ((ICableBusContainerMixin) cb).rple$getColoredBrightness();
        return LightValueColor.fromVanillaLightValue(cb.getLightValue()).rgb16();
    }
}
