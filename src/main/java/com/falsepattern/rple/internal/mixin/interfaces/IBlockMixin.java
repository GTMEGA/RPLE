/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.interfaces;

import com.falsepattern.rple.api.block.RPLEBlockBrightness;
import com.falsepattern.rple.api.block.RPLEBlockTranslucency;
import com.falsepattern.rple.api.color.RPLEColor;
import org.jetbrains.annotations.Nullable;

public interface IBlockMixin extends RPLEBlockBrightness, RPLEBlockTranslucency {
    void rple$initBaseColoredBrightness(@Nullable RPLEColor baseColoredBrightness);

    void rple$initBaseColoredTranslucency(@Nullable RPLEColor baseColoredTranslucency);

    void rple$initMetaColoredBrightness(@Nullable RPLEColor @Nullable [] metaColoredBrightness);

    void rple$initMetaColoredTranslucency(@Nullable RPLEColor @Nullable [] metaColoredTranslucency);
}
