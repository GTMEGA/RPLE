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

package com.falsepattern.rple.internal.mixin.mixins.common.rple;

import com.falsepattern.rple.api.block.RPLEBlockRoot;
import com.falsepattern.rple.api.color.LightValueColor;
import com.falsepattern.rple.api.color.RPLEColor;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import static com.falsepattern.rple.internal.mixin.plugin.MixinPlugin.RPLE_ROOT_MIXIN_PRIORITY;

@Unique
@Mixin(value = Block.class, priority = RPLE_ROOT_MIXIN_PRIORITY)
@SuppressWarnings("unused")
public abstract class RPLEBlockRootImplMixin implements RPLEBlockRoot {
    @Shadow
    public abstract int getLightValue();

    @Shadow(remap = false)
    public abstract int getLightValue(IBlockAccess world, int posX, int posY, int posZ);

    @Shadow
    public abstract int getLightOpacity();

    @Shadow(remap = false)
    public abstract int getLightOpacity(IBlockAccess world, int posX, int posY, int posZ);

    @Dynamic("Initialized in: [com.falsepattern.rple.internal.mixin.mixins.common.rple.RPLEBlockInitImplMixin]")
    private ThreadLocal<Boolean> rple$passInternalBrightness;
    @Dynamic("Initialized in: [com.falsepattern.rple.internal.mixin.mixins.common.rple.RPLEBlockInitImplMixin]")
    private ThreadLocal<Boolean> rple$passInternalOpacity;

    @Override
    public RPLEColor rple$getInternalColoredBrightness() {
        rple$passInternalBrightness.set(true);
        val lightValue = getLightValue();
        return LightValueColor.fromVanillaLightValue(lightValue);
    }

    @Override
    public RPLEColor rple$getInternalColoredBrightness(IBlockAccess world, int posX, int posY, int posZ) {
        rple$passInternalBrightness.set(true);
        val lightValue = getLightValue(world, posX, posY, posZ);
        return LightValueColor.fromVanillaLightValue(lightValue);
    }

    @Override
    public RPLEColor rple$getInternalColoredTranslucency() {
        rple$passInternalOpacity.set(true);
        val lightOpacity = getLightOpacity();
        return LightValueColor.fromVanillaLightOpacity(lightOpacity);
    }

    @Override
    public RPLEColor rple$getInternalColoredTranslucency(IBlockAccess world, int posX, int posY, int posZ) {
        rple$passInternalOpacity.set(true);
        val lightOpacity = getLightOpacity(world, posX, posY, posZ);
        return LightValueColor.fromVanillaLightOpacity(lightOpacity);
    }
}
