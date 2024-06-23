/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.block;

import com.falsepattern.rple.api.common.color.RPLEColor;
import org.jetbrains.annotations.Nullable;

public interface RPLEBlockInit {
    // TODO: [NO_COL_OBJ] Make this take a short
    void rple$initBaseBrightnessColor(@Nullable RPLEColor baseColoredBrightness);

    // TODO: [NO_COL_OBJ] Make this take a short
    void rple$initBaseTranslucencyColor(@Nullable RPLEColor baseColoredTranslucency);

    // TODO: [NO_COL_OBJ] Make this take a short array
    void rple$initMetaBrightnessColors(@Nullable RPLEColor @Nullable [] metaColoredBrightness);

    // TODO: [NO_COL_OBJ] Make this take a short array
    void rple$initMetaTranslucencyColors(@Nullable RPLEColor @Nullable [] metaColoredTranslucency);

    void rple$finishColorInit();
}
