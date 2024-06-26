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
    private short baseBrightnessColor = -1;
    @Setter
    private short baseTranslucencyColor = -1;

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
