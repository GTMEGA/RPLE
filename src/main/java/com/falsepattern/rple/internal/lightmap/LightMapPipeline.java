/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.lightmap;

import com.falsepattern.rple.api.lightmap.*;
import com.falsepattern.rple.internal.Common;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class LightMapPipeline implements LightMapRegistry {
    private static final LightMapPipeline INSTANCE = new LightMapPipeline();

    private final Set<PriorityPair<BlockLightMapBase>> blockBases = new TreeSet<>();
    private final Set<PriorityPair<SkyLightMapBase>> skyBases = new TreeSet<>();
    private final List<BlockLightMapMask> blockMasks = new ArrayList<>();
    private final List<SkyLightMapMask> skyMasks = new ArrayList<>();

    private final LightMap2D mixedLightMap = new LightMap2D();
    private final LightMap1D tempStrip = new LightMap1D();

    public static LightMapPipeline lightMapPipeline() {
        return INSTANCE;
    }

    // region Registration
    @Override
    public void registerBlockLightMapBase(BlockLightMapBase blockBase, int priority) {
        val wrappedGenerator = wrappedWithPriority(blockBase, priority);
        if (blockBases.contains(wrappedGenerator)) {
            Common.LOG.warn("BlockLightMapBase registered twice!", new Throwable());
            return;
        }

        blockBases.add(wrappedGenerator);
    }

    @Override
    public void registerSkyLightMapBase(SkyLightMapBase skyBase, int priority) {
        val wrappedGenerator = wrappedWithPriority(skyBase, priority);
        if (skyBases.contains(wrappedGenerator)) {
            Common.LOG.warn("SkyLightMapBase registered twice!", new Throwable());
            return;
        }

        skyBases.add(wrappedGenerator);
    }

    @Override
    public void registerBlockLightMapMask(BlockLightMapMask blockMask) {
        if (blockMasks.contains(blockMask)) {
            Common.LOG.warn("BlockLightMapMask registered twice!", new Throwable());
            return;
        }

        blockMasks.add(blockMask);
    }

    @Override
    public void registerSkyLightMapMask(SkyLightMapMask skyMask) {
        if (skyMasks.contains(skyMask)) {
            Common.LOG.warn("SkyLightMapMask registered twice!", new Throwable());
            return;
        }

        skyMasks.add(skyMask);
    }
    // endregion

    public int[] updateLightMap(float partialTick) {
        val blockStrip = mixedLightMap.blockLightMap();
        val skyStrip = mixedLightMap.skyLightMap();

        for (val blockBase : blockBases) {
            blockStrip.resetLightMap();
            if (blockBase.value.generateBlockLightMapBase(blockStrip, partialTick))
                break;
        }

        for (val skyBase : skyBases) {
            skyStrip.resetLightMap();
            if (skyBase.value.generateSkyLightMapBase(skyStrip, partialTick))
                break;
        }

        for (val mask : blockMasks) {
            tempStrip.resetLightMap();
            if (mask.generateBlockLightMapMask(tempStrip, partialTick))
                blockStrip.multLightMap(tempStrip);
        }

        for (val mask : skyMasks) {
            tempStrip.resetLightMap();
            if (mask.generateSkyLightMapMask(tempStrip, partialTick))
                skyStrip.multLightMap(tempStrip);
        }

        mixedLightMap.mixLightMaps();
        return mixedLightMap.lightMapRGBData();
    }

    private static <T> PriorityPair<T> wrappedWithPriority(T value, int priority) {
        return new PriorityPair<>(value, priority);
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class PriorityPair<T> implements Comparable<PriorityPair<T>> {
        private final T value;
        private final int priority;

        @Override
        public int compareTo(@NotNull LightMapPipeline.PriorityPair<T> o) {
            return Integer.compare(priority, o.priority);
        }
    }
}
