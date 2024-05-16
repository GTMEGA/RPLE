/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.client.lightmap;

import com.falsepattern.rple.api.client.lightmap.*;
import com.falsepattern.rple.internal.common.collection.PriorityPair;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.var;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.falsepattern.rple.internal.RPLEDefaultValues.registerDefaultLightMaps;
import static com.falsepattern.rple.internal.LogHelper.createLogger;
import static com.falsepattern.rple.internal.client.lightmap.LightMapConstants.LIGHT_MAP_1D_SIZE;
import static com.falsepattern.rple.internal.client.lightmap.LightMapConstants.LIGHT_MAP_2D_SIZE;
import static com.falsepattern.rple.internal.common.collection.PriorityPair.wrappedWithPriority;
import static com.falsepattern.rple.internal.common.event.EventPoster.postLightMapRegistrationEvent;
import static lombok.AccessLevel.PRIVATE;
import static net.minecraft.client.Minecraft.getMinecraft;

@NoArgsConstructor(access = PRIVATE)
public final class LightMapPipeline implements RPLELightMapRegistry {
    private static final Logger LOG = createLogger("LightMapPipeline");

    private static final LightMapPipeline INSTANCE = new LightMapPipeline();

    private final Set<RPLELightMapProvider> lightMapProviders = Collections.newSetFromMap(new IdentityHashMap<>());

    private final Set<PriorityPair<RPLEBlockLightMapBase>> blockBases = new TreeSet<>();
    private final Set<PriorityPair<RPLESkyLightMapBase>> skyBases = new TreeSet<>();
    private final List<RPLEBlockLightMapMask> blockMasks = new ArrayList<>();
    private final List<RPLESkyLightMapMask> skyMasks = new ArrayList<>();

    private final LightMapStrip blockLightMapStrip = new LightMapStrip();
    private final LightMapStrip skyLightMapStrip = new LightMapStrip();
    private final LightMapStrip tempLightMapStrip = new LightMapStrip();

    private final int[] mixedLightMapData = new int[LIGHT_MAP_2D_SIZE];

    private boolean registryLocked = false;

    public static LightMapPipeline lightMapPipeline() {
        return INSTANCE;
    }

    public void registerLightMapProviders() {
        if (registryLocked)
            return;

        registerDefaultLightMaps(this);
        postLightMapRegistrationEvent(this);

        registryLocked = true;
        val totalLightMapProviders = lightMapProviders.size();
        if (totalLightMapProviders == 0) {
            LOG.error("No light map providers registered, this will result in the world being full bright");
        }
        if (totalLightMapProviders == 1) {
            LOG.info("Registered 1 light map provider");
        } else {
            LOG.info("Registered {} light map providers", totalLightMapProviders);
        }
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

    public int[] update(float partialTick) {
        for (val blockBase : blockBases) {
            blockLightMapStrip.resetLightMap();
            if (blockBase.value().generateBlockLightMapBase(blockLightMapStrip, partialTick))
                break;
        }

        for (val skyBase : skyBases) {
            skyLightMapStrip.resetLightMap();
            if (skyBase.value().generateSkyLightMapBase(skyLightMapStrip, partialTick))
                break;
        }

        for (val mask : blockMasks) {
            tempLightMapStrip.resetLightMap();
            if (mask.generateBlockLightMapMask(tempLightMapStrip, partialTick))
                blockLightMapStrip.multLightMap(tempLightMapStrip);
        }

        for (val mask : skyMasks) {
            tempLightMapStrip.resetLightMap();
            if (mask.generateSkyLightMapMask(tempLightMapStrip, partialTick))
                skyLightMapStrip.multLightMap(tempLightMapStrip);
        }

        mixLightMaps();
        return mixedLightMapData;
    }

    private void mixLightMaps() {
        val blockLightMapRed = blockLightMapStrip.lightMapRedData();
        val blockLightMapGreen = blockLightMapStrip.lightMapGreenData();
        val blockLightMapBlue = blockLightMapStrip.lightMapBlueData();

        val skyLightMapRed = skyLightMapStrip.lightMapRedData();
        val skyLightMapGreen = skyLightMapStrip.lightMapGreenData();
        val skyLightMapBlue = skyLightMapStrip.lightMapBlueData();

        val gamma = getMinecraft().gameSettings.gammaSetting;

        for (var index = 0; index < LIGHT_MAP_2D_SIZE; index++) {
            val blockIndex = index % LIGHT_MAP_1D_SIZE;
            val skyIndex = index / LIGHT_MAP_1D_SIZE;

            var red = blockLightMapRed[blockIndex] + skyLightMapRed[skyIndex];
            var green = blockLightMapGreen[blockIndex] + skyLightMapGreen[skyIndex];
            var blue = blockLightMapBlue[blockIndex] + skyLightMapBlue[skyIndex];

            red = gammaCorrect(red, gamma);
            green = gammaCorrect(green, gamma);
            blue = gammaCorrect(blue, gamma);

            mixedLightMapData[index] = colorToInt(red, green, blue);
        }
    }

    private static float gammaCorrect(float color, float gamma) {
        color = clamp(color);

        var colorPreGamma = 1F - color;
        colorPreGamma = 1F - (colorPreGamma * colorPreGamma * colorPreGamma * colorPreGamma);

        color = (color * (1F - gamma)) + (colorPreGamma * gamma);
        color = (color * 0.96F) + 0.03F;

        return color;
    }

    private static float clamp(float value) {
        return Math.max(Math.min(value, 1F), 0F);
    }

    private static int colorToInt(float red, float green, float blue) {
        val redByte = colorToByte(red) << 16;
        val greenByte = colorToByte(green) << 8;
        val blueByte = colorToByte(blue);

        return 0xFF000000 | redByte | greenByte | blueByte;
    }

    private static int colorToByte(float color) {
        return Math.round(color * 255F) & 0xFF;
    }
}
