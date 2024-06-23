/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.rple;

import com.falsepattern.rple.api.common.ServerColorHelper;
import com.falsepattern.rple.internal.common.block.RPLEBlockInit;
import lombok.val;
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
    @Unique
    private short rple$rawBaseBrightnessColor;
    @Unique
    private short rple$rawBaseOpacityColor;
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    @Unique
    private short @Nullable [] rple$rawMetaBrightnessColors;
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    @Unique
    private short @Nullable [] rple$rawMetaOpacityColors;

    @Inject(method = "<init>",
            at = @At("RETURN"),
            require = 1)
    private void rpleBlockInit(Material material, CallbackInfo ci) {
        this.rple$rawBaseBrightnessColor = -1;
        this.rple$rawBaseOpacityColor = -1;
        this.rple$rawMetaBrightnessColors = null;
        this.rple$rawMetaOpacityColors = null;
    }

    @Override
    public void rple$initBaseBrightnessColor(short baseColoredBrightness) {
        rple$rawBaseBrightnessColor = baseColoredBrightness;
    }

    @Override
    public void rple$initBaseTranslucencyColor(short baseColoredTranslucency) {
        if (baseColoredTranslucency != -1) {
            rple$rawBaseOpacityColor = ServerColorHelper.RGB16OpacityTranslucentSwap(baseColoredTranslucency);
        } else {
            rple$rawBaseOpacityColor = -1;
        }
    }

    @Override
    public void rple$initMetaBrightnessColors(short @Nullable [] metaColoredBrightness) {
        if (metaColoredBrightness == null) {
            rple$rawMetaBrightnessColors = null;
        } else {
            rple$rawMetaBrightnessColors = new short[metaColoredBrightness.length];
            System.arraycopy(metaColoredBrightness, 0, rple$rawMetaBrightnessColors, 0, metaColoredBrightness.length);
        }
    }

    @Override
    public void rple$initMetaTranslucencyColors(short @Nullable [] metaColoredTranslucency) {
        if (metaColoredTranslucency == null) {
            rple$rawMetaOpacityColors = null;
        } else {
            rple$rawMetaOpacityColors = new short[metaColoredTranslucency.length];
            for (int i = 0; i < metaColoredTranslucency.length; i++) {
                val op = metaColoredTranslucency[i];
                if (op == -1) {
                    rple$rawMetaOpacityColors[i] = -1;
                } else {
                    rple$rawMetaOpacityColors[i] = ServerColorHelper.RGB16OpacityTranslucentSwap(op);
                }
            }
        }
    }

    @Override
    public void rple$finishColorInit() {
        // TODO: [NO_COL_OBJ] Remove this, or make 'finish' optimise a fast path
    }
}
