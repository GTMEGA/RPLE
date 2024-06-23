/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.rple;

import com.falsepattern.rple.api.common.RGB16Helper;
import com.falsepattern.rple.api.common.color.RPLEColor;
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
    private short rple$rawBaseBrightnessColor;
    private short rple$rawBaseOpacityColor;
    private short @Nullable [] rple$rawMetaBrightnessColors;
    private short @Nullable [] rple$rawMetaOpacityColors;

    @Nullable
    @Deprecated
    private RPLEColor rple$baseBrightnessColor;
    @Nullable
    @Deprecated
    private RPLEColor rple$baseTranslucencyColor;
    @Nullable
    @Deprecated
    private RPLEColor @Nullable [] rple$metaBrightnessColors;
    @Nullable
    @Deprecated
    private RPLEColor @Nullable [] rple$metaTranslucencyColors;

    @Inject(method = "<init>",
            at = @At("RETURN"),
            require = 1)
    private void rpleBlockInit(Material material, CallbackInfo ci) {
        this.rple$rawBaseBrightnessColor = -1;
        this.rple$rawBaseOpacityColor = -1;
        this.rple$rawMetaBrightnessColors = null;
        this.rple$rawMetaOpacityColors = null;

        this.rple$baseBrightnessColor = null;
        this.rple$baseTranslucencyColor = null;
        this.rple$metaBrightnessColors = null;
        this.rple$metaTranslucencyColors = null;
    }

    @Override
    public void rple$initBaseBrightnessColor(@Nullable RPLEColor baseColoredBrightness) {
        rple$baseBrightnessColor = baseColoredBrightness;
        if (baseColoredBrightness == null) {
            rple$rawBaseBrightnessColor = -1;
        } else {
            rple$rawBaseBrightnessColor = RGB16Helper.brightnessToCache(baseColoredBrightness);
        }
    }

    @Override
    public void rple$initBaseTranslucencyColor(@Nullable RPLEColor baseColoredTranslucency) {
        rple$baseTranslucencyColor = baseColoredTranslucency;
        if (baseColoredTranslucency == null) {
            rple$rawBaseOpacityColor = -1;
        } else {
            rple$rawBaseOpacityColor = RGB16Helper.translucencyToOpacityCache(baseColoredTranslucency);
        }
    }

    @Override
    public void rple$initMetaBrightnessColors(@Nullable RPLEColor @Nullable [] metaColoredBrightness) {
        rple$metaBrightnessColors = metaColoredBrightness;
        if (metaColoredBrightness == null) {
            rple$rawMetaBrightnessColors = null;
        } else {
            rple$rawMetaBrightnessColors = new short[metaColoredBrightness.length];
            for (int i = 0; i < metaColoredBrightness.length; i++) {
                val br = metaColoredBrightness[i];
                if (br == null) {
                    rple$rawMetaBrightnessColors[i] = -1;
                } else {
                    rple$rawMetaBrightnessColors[i] = RGB16Helper.brightnessToCache(br);
                }
            }
        }
    }

    @Override
    public void rple$initMetaTranslucencyColors(@Nullable RPLEColor @Nullable [] metaColoredTranslucency) {
        rple$metaTranslucencyColors = metaColoredTranslucency;
        if (metaColoredTranslucency == null) {
            rple$rawMetaOpacityColors = null;
        } else {
            rple$rawMetaOpacityColors = new short[metaColoredTranslucency.length];
            for (int i = 0; i < metaColoredTranslucency.length; i++) {
                val op = metaColoredTranslucency[i];
                if (op == null) {
                    rple$rawMetaOpacityColors[i] = -1;
                } else {
                    rple$rawMetaOpacityColors[i] = RGB16Helper.translucencyToOpacityCache(op);
                }
            }
        }
    }

    @Override
    public void rple$finishColorInit() {
        // TODO: [NO_COL_OBJ] Remove this, or make 'finish' optimise a fast path
    }
}
