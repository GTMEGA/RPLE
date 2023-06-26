/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.block;

import com.falsepattern.rple.api.color.RPLEColor;
import com.falsepattern.rple.internal.config.container.BlockReference;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Accessors(fluent = true, chain = false)
public final class BlockColor {
    @Getter
    private final Block block;

    @Nullable
    private final RPLEColor baseColor;
    @Nullable
    private final RPLEColor[] blockMetaColors;

    @Getter
    private final boolean isValid;

    public BlockColor(Block block, Map<BlockReference, RPLEColor> blocks) {
        this.block = block;

        RPLEColor baseColor = null;
        RPLEColor[] blockMetaColors = null;

        var isValid = false;

        try {
            if (blocks.isEmpty())
                throw new IllegalArgumentException();

            val baseBlockReference = new BlockReference(block);
            var biggestMeta = -1;
            val blocksWithMeta = new TIntObjectHashMap<RPLEColor>();
            for (val blockColor : blocks.entrySet()) {
                val blockReference = blockColor.getKey();
                if (!baseBlockReference.isSameBlockType(blockReference))
                    continue;

                val color = blockColor.getValue();
                val blockMeta = blockReference.meta();
                if (blockMeta.isPresent()) {
                    final int meta = blockMeta.get();
                    if (meta > biggestMeta)
                        biggestMeta = meta;

                    blocksWithMeta.put(meta, color);
                } else {
                    baseColor = color;
                }
            }

            if (biggestMeta >= 0) {
                blockMetaColors = new RPLEColor[biggestMeta + 1];

                val iterator = blocksWithMeta.iterator();
                while (iterator.hasNext()) {
                    val meta = iterator.key();
                    val color = iterator.value();
                    blockMetaColors[meta] = color;
                }
            }

            isValid = true;
        } catch (IllegalArgumentException e) {
            baseColor = null;
            blockMetaColors = null;

            isValid = false;
        }

        this.baseColor = baseColor;
        this.blockMetaColors = blockMetaColors;

        this.isValid = isValid;
    }

    public @Nullable RPLEColor ofBlockMeta(int blockMeta) {
        val metaColor = lookupMetaColor(blockMeta);
        if (metaColor != null)
            return metaColor;
        return baseColor;
    }

    private @Nullable RPLEColor lookupMetaColor(int blockMeta) {
        if (blockMeta < 0)
            return null;
        if (blockMetaColors == null)
            return null;
        if (blockMeta >= blockMetaColors.length)
            return null;

        return blockMetaColors[blockMeta];
    }
}
