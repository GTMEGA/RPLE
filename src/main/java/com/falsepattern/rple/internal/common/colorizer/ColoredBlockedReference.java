/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.colorizer;

import com.falsepattern.rple.internal.common.block.RPLEBlockInit;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TIntShortMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TIntShortHashMap;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@Accessors(fluent = true, chain = false)
@RequiredArgsConstructor
public final class ColoredBlockedReference {
    private final RPLEBlockInit block;

    @Setter
    private short baseBrightnessColor;
    @Setter
    private short baseTranslucencyColor;

    private final TIntShortMap metaBrightnessColorsMap = new TIntShortHashMap();
    private final TIntShortMap metaTranslucencyColorsMap = new TIntShortHashMap();

    public void metaBrightnessColorsMap(int blockMeta, short color) {
        if (blockMeta >= 0)
            metaBrightnessColorsMap.put(blockMeta, color);
    }

    public void metaTranslucencyColorsMap(int blockMeta, short color) {
        if (blockMeta >= 0)
            metaTranslucencyColorsMap.put(blockMeta, color);
    }

    public void apply() {
        block.rple$initBaseBrightnessColor(baseBrightnessColor);
        block.rple$initBaseTranslucencyColor(baseTranslucencyColor);
        block.rple$initMetaBrightnessColors(metaColorsArrayFromMap(metaBrightnessColorsMap));
        block.rple$initMetaTranslucencyColors(metaColorsArrayFromMap(metaTranslucencyColorsMap));
        block.rple$finishColorInit();
    }

    private short @Nullable [] metaColorsArrayFromMap(TIntShortMap metaColorsMap) {
        if (metaColorsMap.isEmpty())
            return null;
        val blockMetas = metaColorsMap.keys();
        val maxBlockMeta = Arrays.stream(blockMetas)
                                 .max()
                                 .orElse(-1);
        if (maxBlockMeta < 0)
            return null;

        val metaColorsLength = maxBlockMeta + 1;
        val metaColors = new short[metaColorsLength];
        for (val blockMeta : blockMetas)
            metaColors[blockMeta] = metaColorsMap.get(blockMeta);
        return metaColors;
    }
}
