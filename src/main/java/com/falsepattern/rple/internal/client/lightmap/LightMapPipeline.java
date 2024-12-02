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

package com.falsepattern.rple.internal.client.lightmap;

import com.falsepattern.lib.util.MathUtil;
import com.falsepattern.rple.api.client.lightmap.*;
import com.falsepattern.rple.internal.common.collection.PriorityPair;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.var;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.falsepattern.rple.internal.RPLEDefaultValues.registerDefaultLightMaps;
import static com.falsepattern.rple.internal.common.util.LogHelper.createLogger;
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

    private final List<PriorityPair<RPLEBlockLightMapBase>> blockBases = new ArrayList<>();
    private final List<PriorityPair<RPLESkyLightMapBase>> skyBases = new ArrayList<>();
    private final List<PriorityPair<RPLEBlockLightMapMask>> blockMasks = new ArrayList<>();
    private final List<PriorityPair<RPLESkyLightMapMask>> skyMasks = new ArrayList<>();

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

        blockBases.sort(Comparator.naturalOrder());
        skyBases.sort(Comparator.naturalOrder());
        blockMasks.sort(Comparator.naturalOrder());
        skyMasks.sort(Comparator.naturalOrder());
    }

    // region Registration
    @Override
    @Deprecated
    public void registerLightMapGenerator(@NotNull RPLELightMapGenerator generator, int priority) {
        if (!registerLightMapProvider(generator))
            return;

        blockBases.add(wrappedWithPriority(generator, priority));
        skyBases.add(wrappedWithPriority(generator, priority));
        blockMasks.add(wrappedWithPriority(generator, priority));
        skyMasks.add(wrappedWithPriority(generator, priority));
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
    public void registerLightMapMask(@NotNull RPLELightMapMask mask, int sortOrder) {
        if (!registerLightMapProvider(mask))
            return;

        blockMasks.add(wrappedWithPriority(mask, sortOrder));
        skyMasks.add(wrappedWithPriority(mask, sortOrder));
    }

    @Override
    public void registerBlockLightMapMask(@NotNull RPLEBlockLightMapMask blockMask, int sortOrder) {
        if (!registerLightMapProvider(blockMask))
            return;

        blockMasks.add(wrappedWithPriority(blockMask, sortOrder));
    }

    @Override
    public void registerSkyLightMapMask(@NotNull RPLESkyLightMapMask skyMask, int sortOrder) {
        if (!registerLightMapProvider(skyMask))
            return;

        skyMasks.add(wrappedWithPriority(skyMask, sortOrder));
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
            val m = mask.value();
            //noinspection deprecation
            if (m.generateBlockLightMapMask(tempLightMapStrip, partialTick))
                blockLightMapStrip.mulLightMap(tempLightMapStrip);
            else
                m.mutateBlockLightMap(blockLightMapStrip, partialTick);

        }

        for (val mask : skyMasks) {
            tempLightMapStrip.resetLightMap();
            val m = mask.value();
            //noinspection deprecation
            if (m.generateSkyLightMapMask(tempLightMapStrip, partialTick))
                skyLightMapStrip.mulLightMap(tempLightMapStrip);
            else
                m.mutateSkyLightMap(skyLightMapStrip, partialTick);
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

            val bR = MathUtil.clamp(blockLightMapRed[blockIndex], 0, 1);
            val bG = MathUtil.clamp(blockLightMapGreen[blockIndex], 0, 1);
            val bB = MathUtil.clamp(blockLightMapBlue[blockIndex], 0, 1);

            val sR = MathUtil.clamp(skyLightMapRed[skyIndex], 0, 1);
            val sG = MathUtil.clamp(skyLightMapGreen[skyIndex], 0, 1);
            val sB = MathUtil.clamp(skyLightMapBlue[skyIndex], 0, 1);

            val r = MathUtil.clamp(bR + sR, 0, 1);
            val g = MathUtil.clamp(bG + sG, 0, 1);
            val b = MathUtil.clamp(bB + sB, 0, 1);

            mixedLightMapData[index] = colorToInt(r, g, b);
        }
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
