/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.client.lightmap;

import com.falsepattern.rple.api.lightmap.*;
import com.falsepattern.rple.internal.collection.PriorityPair;
import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.falsepattern.rple.internal.RightProperLightingEngine.createLogger;
import static com.falsepattern.rple.internal.collection.PriorityPair.wrappedWithPriority;
import static com.falsepattern.rple.internal.common.RPLEDefaultValues.registerDefaultLightMaps;
import static com.falsepattern.rple.internal.event.EventPoster.postLightMapRegistrationEvent;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class LightMapPipeline implements RPLELightMapRegistry {
    private static final Logger LOG = createLogger("Light Map Pipeline");

    private static final LightMapPipeline INSTANCE = new LightMapPipeline();

    private final Set<RPLELightMapProvider> lightMapProviders = Collections.newSetFromMap(new IdentityHashMap<>());

    private final Set<PriorityPair<RPLEBlockLightMapBase>> blockBases = new TreeSet<>();
    private final Set<PriorityPair<RPLESkyLightMapBase>> skyBases = new TreeSet<>();
    private final List<RPLEBlockLightMapMask> blockMasks = new ArrayList<>();
    private final List<RPLESkyLightMapMask> skyMasks = new ArrayList<>();

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
    public void registerLightMapGenerator(@NotNull RPLELightMapGenerator generator, int priority) {
        if (!registerLightMapProvider(generator))
            return;

        blockBases.add(wrappedWithPriority(generator, priority));
        skyBases.add(wrappedWithPriority(generator, priority));
        blockMasks.add(generator);
        skyMasks.add(generator);
    }

    @Override
    public void registerLightMapBase(@NotNull RPLELightMapBase base, int priority) {
        if (!registerLightMapProvider(base))
            return;

        blockBases.add(wrappedWithPriority(base, priority));
        skyBases.add(wrappedWithPriority(base, priority));
    }

    @Override
    public void registerBlockLightMapBase(@NotNull RPLEBlockLightMapBase blockBase, int priority) {
        if (!registerLightMapProvider(blockBase))
            return;

        blockBases.add(wrappedWithPriority(blockBase, priority));
    }

    @Override
    public void registerSkyLightMapBase(@NotNull RPLESkyLightMapBase skyBase, int priority) {
        if (!registerLightMapProvider(skyBase))
            return;

        skyBases.add(wrappedWithPriority(skyBase, priority));
    }

    @Override
    public void registerLightMapMask(@NotNull RPLELightMapMask mask) {
        if (!registerLightMapProvider(mask))
            return;

        blockMasks.add(mask);
        skyMasks.add(mask);
    }

    @Override
    public void registerBlockLightMapMask(@NotNull RPLEBlockLightMapMask blockMask) {
        if (!registerLightMapProvider(blockMask))
            return;

        blockMasks.add(blockMask);
    }

    @Override
    public void registerSkyLightMapMask(@NotNull RPLESkyLightMapMask skyMask) {
        if (!registerLightMapProvider(skyMask))
            return;

        skyMasks.add(skyMask);
    }

    private boolean registerLightMapProvider(RPLELightMapProvider provider) {
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
    // endregion

    public int[] updateLightMap(float partialTick) {
        val blockStrip = mixedLightMap.blockLightMap();
        val skyStrip = mixedLightMap.skyLightMap();

        for (val blockBase : blockBases) {
            blockStrip.resetLightMap();
            if (blockBase.value().generateBlockLightMapBase(blockStrip, partialTick))
                break;
        }

        for (val skyBase : skyBases) {
            skyStrip.resetLightMap();
            if (skyBase.value().generateSkyLightMapBase(skyStrip, partialTick))
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
}
