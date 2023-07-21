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

package com.falsepattern.rple.internal.mixin.mixins.common;

import com.falsepattern.rple.internal.mixin.hook.ColoredLightingHooks;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Unique
@Mixin(Block.class)
@SuppressWarnings("unused")
public abstract class BlockMixin {
    @Dynamic("Initialized in: [com.falsepattern.rple.internal.mixin.mixins.common.rple.RPLEBlockInitImplMixin]")
    private ThreadLocal<Boolean> rple$passInternalBrightness;
    @Dynamic("Initialized in: [com.falsepattern.rple.internal.mixin.mixins.common.rple.RPLEBlockInitImplMixin]")
    private ThreadLocal<Boolean> rple$passInternalOpacity;

    @Inject(method = "getLightValue()I",
            at = @At("HEAD"),
            cancellable = true,
            require = 1)
    private void getLightValue(CallbackInfoReturnable<Integer> cir) {
        if (rple$passInternalBrightness.get()) {
            rple$passInternalBrightness.set(false);
        } else {
            cir.setReturnValue(ColoredLightingHooks.getLightValue(thiz()));
        }
    }

    @Inject(method = "getLightValue(Lnet/minecraft/world/IBlockAccess;III)I",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 1)
    private void getLightValue(IBlockAccess world,
                               int posX,
                               int posY,
                               int posZ,
                               CallbackInfoReturnable<Integer> cir) {
        if (rple$passInternalBrightness.get()) {
            rple$passInternalBrightness.set(false);
        } else {
            cir.setReturnValue(ColoredLightingHooks.getLightValue(world, thiz(), posX, posY, posZ));
        }
    }

    @Inject(method = "getLightOpacity()I",
            at = @At("HEAD"),
            cancellable = true,
            require = 1)
    private void getLightOpacity(CallbackInfoReturnable<Integer> cir) {
        if (rple$passInternalOpacity.get()) {
            rple$passInternalOpacity.set(false);
        } else {
            cir.setReturnValue(ColoredLightingHooks.getLightOpacity(thiz()));
        }
    }

    @Inject(method = "getLightOpacity(Lnet/minecraft/world/IBlockAccess;III)I",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 1)
    private void getLightOpacity(IBlockAccess world,
                                 int posX,
                                 int posY,
                                 int posZ,
                                 CallbackInfoReturnable<Integer> cir) {
        if (rple$passInternalOpacity.get()) {
            rple$passInternalOpacity.set(false);
        } else {
            cir.setReturnValue(ColoredLightingHooks.getLightOpacity(world, thiz(), posX, posY, posZ));
        }
    }

    private Block thiz() {
        return (Block) (Object) this;
    }
}
