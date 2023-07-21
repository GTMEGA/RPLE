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

import com.falsepattern.rple.api.color.RPLEColor;
import com.falsepattern.rple.internal.mixin.interfaces.RPLEBlockInit;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.falsepattern.rple.internal.mixin.plugin.MixinPlugin.RPLE_INIT_MIXIN_PRIORITY;

@Unique
@Mixin(value = Block.class, priority = RPLE_INIT_MIXIN_PRIORITY)
@SuppressWarnings("unused")
public abstract class RPLEBlockInitImplMixin implements RPLEBlockInit {
    @Nullable
    private RPLEColor rple$baseBrightnessColor;
    @Nullable
    private RPLEColor rple$baseTranslucencyColor;
    @Nullable
    private RPLEColor @Nullable [] rple$metaBrightnessColors;
    @Nullable
    private RPLEColor @Nullable [] rple$metaTranslucencyColors;

    private ThreadLocal<Boolean> rple$passInternalBrightness;
    private ThreadLocal<Boolean> rple$passInternalOpacity;

    @Inject(method = "<init>",
            at = @At("RETURN"),
            require = 1)
    private void rpleBlockInit(Material material, CallbackInfo ci) {
        this.rple$baseBrightnessColor = null;
        this.rple$baseTranslucencyColor = null;
        this.rple$metaBrightnessColors = null;
        this.rple$metaTranslucencyColors = null;

        this.rple$passInternalBrightness = ThreadLocal.withInitial(() -> false);
        this.rple$passInternalOpacity = ThreadLocal.withInitial(() -> false);
    }

    @Override
    public void rple$initBaseBrightnessColor(@Nullable RPLEColor baseColoredBrightness) {
        rple$baseBrightnessColor = baseColoredBrightness;
    }

    @Override
    public void rple$initBaseTranslucencyColor(@Nullable RPLEColor baseColoredTranslucency) {
        rple$baseTranslucencyColor = baseColoredTranslucency;
    }

    @Override
    public void rple$initMetaBrightnessColors(@Nullable RPLEColor @Nullable [] metaColoredBrightness) {
        rple$metaBrightnessColors = metaColoredBrightness;
    }

    @Override
    public void rple$initMetaTranslucencyColors(@Nullable RPLEColor @Nullable [] metaColoredTranslucency) {
        rple$metaTranslucencyColors = metaColoredTranslucency;
    }
}
