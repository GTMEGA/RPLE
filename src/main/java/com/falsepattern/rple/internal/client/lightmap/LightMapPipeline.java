/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.client.lightmap;

import com.falsepattern.rple.api.lightmap.*;
import com.falsepattern.rple.internal.Tags;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.falsepattern.rple.internal.common.RPLEDefaultValues.registerDefaultLightMaps;
import static com.falsepattern.rple.internal.event.EventPoster.postLightMapRegistrationEvent;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class LightMapPipeline implements LightMapRegistry {
    private static final Logger LOG = LogManager.getLogger(Tags.MOD_NAME + "|" + "Light Map Pipeline");

    private static final LightMapPipeline INSTANCE = new LightMapPipeline();

    private final Set<LightMapProvider> lightMapProviders = Collections.newSetFromMap(new IdentityHashMap<>());

    private final Set<PriorityPair<BlockLightMapBase>> blockBases = new TreeSet<>();
    private final Set<PriorityPair<SkyLightMapBase>> skyBases = new TreeSet<>();
    private final List<BlockLightMapMask> blockMasks = new ArrayList<>();
    private final List<SkyLightMapMask> skyMasks = new ArrayList<>();

    private final LightMap2D mixedLightMap = new LightMap2D();
    private final LightMap1D tempStrip = new LightMap1D();

    private boolean registryLocked = false;

    public static LightMapPipeline lightMapPipeline() {
        return INSTANCE;
    }

    public void registerLightMaps() {
        if (registryLocked)
            return;

        registerDefaultLightMaps(this);
        postLightMapRegistrationEvent(this);

        registryLocked = true;
    }

    // region Registration

    @Override
    public void registerLightMapGenerator(LightMapGenerator generator, int priority) {
        if (!registerLightMapProvider(generator))
            return;

        blockBases.add(wrappedWithPriority(generator, priority));
        skyBases.add(wrappedWithPriority(generator, priority));
        blockMasks.add(generator);
        skyMasks.add(generator);
    }

    @Override
    public void registerLightMapBase(LightMapBase base, int priority) {
        if (!registerLightMapProvider(base))
            return;

        blockBases.add(new PriorityPair<>(base, priority));
        skyBases.add(wrappedWithPriority(base, priority));
    }

    @Override
    public void registerBlockLightMapBase(BlockLightMapBase blockBase, int priority) {
        if (!registerLightMapProvider(blockBase))
            return;

        blockBases.add(wrappedWithPriority(blockBase, priority));
    }

    @Override
    public void registerSkyLightMapBase(SkyLightMapBase skyBase, int priority) {
        if (!registerLightMapProvider(skyBase))
            return;

        skyBases.add(wrappedWithPriority(skyBase, priority));
    }

    @Override
    public void registerLightMapMask(LightMapMask mask) {
        if (!registerLightMapProvider(mask))
            return;

        blockMasks.add(mask);
        skyMasks.add(mask);
    }

    @Override
    public void registerBlockLightMapMask(BlockLightMapMask blockMask) {
        if (!registerLightMapProvider(blockMask))
            return;

        blockMasks.add(blockMask);
    }

    @Override
    public void registerSkyLightMapMask(SkyLightMapMask skyMask) {
        if (!registerLightMapProvider(skyMask))
            return;

        skyMasks.add(skyMask);
    }

    private boolean registerLightMapProvider(LightMapProvider provider) {
        if (registryLocked) {
            LOG.error("Failed to register light map provider after post init", new Throwable());
            return false;
        }
        if (provider == null) {
            LOG.error("Light map provider can't be null", new Throwable());
            return false;
        }
        if (lightMapProviders.contains(provider)) {
            LOG.error("Tried to register light map provider twice", new Throwable());
            return false;
        }

        lightMapProviders.add(provider);
        return true;
    }

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

    @EqualsAndHashCode
    @RequiredArgsConstructor(access = PRIVATE)
    private static class PriorityPair<T> implements Comparable<PriorityPair<T>> {
        private final T value;
        private final int priority;

        @Override
        public int compareTo(@NotNull LightMapPipeline.PriorityPair<T> o) {
            return Integer.compare(priority, o.priority);
        }
    }

    private static <T> PriorityPair<T> wrappedWithPriority(T value, int priority) {
        return new PriorityPair<>(value, priority);
    }
}
