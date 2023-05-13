/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.lightmap;

import com.falsepattern.rple.api.lightmap.LightMapChannel;
import com.falsepattern.rple.api.lightmap.LightMapBase;
import com.falsepattern.rple.api.lightmap.LightMapMask;
import com.falsepattern.rple.api.lightmap.LightMapMaskType;
import com.falsepattern.rple.api.lightmap.LightMapPipelineRegistry;
import lombok.Getter;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class LightMapPipeline implements LightMapPipelineRegistry {
    public static final LightMapPipeline INSTANCE = new LightMapPipeline();

    private final List<LightMapMask> blockMasks = new ArrayList<>();
    private final List<LightMapMask> skyMasks = new ArrayList<>();

    private final Map<LightMapBase, Integer> basePriorities = new IdentityHashMap<>();
    private final List<LightMapBase> bases = new ArrayList<>();

    @Getter
    private final LightMapChannel accumulatorBlock = new LightMapChannel();
    @Getter
    private final LightMapChannel accumulatorSky = new LightMapChannel();
    private final LightMapChannel scratch = new LightMapChannel();

    @Override
    public void registerBase(LightMapBase base, int priority) {
        int i = bases.size() - 1;
        for (; i >= 0; i--) {
            int basePr = basePriorities.get(bases.get(i));
            if (basePr >= priority) {
                i++;
                break;
            }
        }
        bases.add(base);
        basePriorities.put(base, priority);
    }

    @Override
    public void registerMask(LightMapMask mask, LightMapMaskType type) {
        switch (type) {
            case BLOCK:
                blockMasks.add(mask);
                return;
            case SKY:
                skyMasks.add(mask);
                return;
        }
    }

    private static void reset(LightMapChannel lightMap) {
        Arrays.fill(lightMap.R, 1);
        Arrays.fill(lightMap.G, 1);
        Arrays.fill(lightMap.B, 1);
    }

    private static void multiply(float[] accum, float[] scratch) {
        for (int i = 0; i < LightMapChannel.LIGHT_MAP_SIZE; i++) {
            accum[i] *= scratch[i];
        }
    }

    private void multiplyAccumulatorAndScratch(LightMapChannel accumulator) {
        multiply(accumulator.R, scratch.R);
        multiply(accumulator.G, scratch.G);
        multiply(accumulator.B, scratch.B);
    }

    void updateLightMap(float partialTickTime) {
        reset(accumulatorSky);
        reset(accumulatorBlock);
        for (val base: bases) {
            if (base.enabled()) {
                base.generateBaseBlock(accumulatorBlock, partialTickTime);
                base.generateBaseSky(accumulatorSky, partialTickTime);
                break;
            }
        }
        for (val mask: blockMasks) {
            reset(scratch);
            mask.generateMask(scratch, partialTickTime);
            multiplyAccumulatorAndScratch(accumulatorBlock);
        }
        for (val mask: skyMasks) {
            reset(scratch);
            mask.generateMask(scratch, partialTickTime);
            multiplyAccumulatorAndScratch(accumulatorSky);
        }
    }
}
