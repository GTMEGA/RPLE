/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.block;

import com.falsepattern.rple.api.color.RPLEColor;
import com.falsepattern.rple.internal.mixin.interfaces.RPLEBlockInit;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.Nullable;

@Accessors(fluent = true, chain = false)
@RequiredArgsConstructor
public final class ColoredBlockedReference {
    private final RPLEBlockInit block;

    @Setter
    private @Nullable RPLEColor brightness;
    @Setter
    private @Nullable RPLEColor translucency;

    private final TIntObjectMap<RPLEColor> metaBrightness = new TIntObjectHashMap<>();
    private final TIntObjectMap<RPLEColor> metaTranslucency = new TIntObjectHashMap<>();

    public void metaBrightness(int meta, RPLEColor brightness) {
        if (meta >= 0)
            metaBrightness.put(meta, brightness);
    }

    public void metaTranslucency(int meta, RPLEColor translucency) {
        if (meta >= 0)
            metaTranslucency.put(meta, translucency);
    }

    public void apply() {
        block.rple$initBaseColoredBrightness(brightness);
        block.rple$initBaseColoredTranslucency(translucency);
        block.rple$initMetaColoredBrightness(metaColors(metaBrightness));
        block.rple$initMetaColoredTranslucency(metaColors(metaTranslucency));
    }

    private @Nullable RPLEColor @Nullable [] metaColors(TIntObjectMap<RPLEColor> metaColorMap) {
        if (metaColorMap.isEmpty())
            return null;

        val metas = metaColorMap.keys();
        val size = metaArraySize(metaColorMap.keys());

        val metaColors = new RPLEColor[size];
        for (val meta : metas)
            metaColors[meta] = metaColorMap.get(meta);

        return metaColors;
    }

    private int metaArraySize(int[] metas) {
        var max = -1;
        for (val value : metas)
            if (value > max)
                max = value;
        return max + 1;
    }
}
