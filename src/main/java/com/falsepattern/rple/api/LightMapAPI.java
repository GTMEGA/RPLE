/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api;

import com.falsepattern.rple.api.lightmap.*;
import com.falsepattern.rple.internal.lightmap.LightMapPipeline;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class LightMapAPI {
    public static void registerLightMapGenerator(LightMapGenerator generator, int priority) {
        lightMapPipelineRegistry().registerLightMapGenerator(generator, priority);
    }

    public static void registerLightMapBase(LightMapBase base, int priority) {
        lightMapPipelineRegistry().registerLightMapBase(base, priority);
    }

    public static void registerBlockLightMapBase(BlockLightMapBase blockBase, int priority) {
        lightMapPipelineRegistry().registerBlockLightMapBase(blockBase, priority);
    }

    public static void registerSkyLightMapBase(SkyLightMapBase skyBase, int priority) {
        lightMapPipelineRegistry().registerSkyLightMapBase(skyBase, priority);
    }

    public static void registerLightMapMask(LightMapMask mask) {
        lightMapPipelineRegistry().registerLightMapMask(mask);
    }

    public static void registerBlockLightMapMask(BlockLightMapMask blockMask) {
        lightMapPipelineRegistry().registerBlockLightMapMask(blockMask);
    }

    public static void registerSkyLightMapMask(SkyLightMapMask skyMask) {
        lightMapPipelineRegistry().registerSkyLightMapMask(skyMask);
    }

    public static LightMapRegistry lightMapPipelineRegistry() {
        return LightMapPipeline.lightMapPipeline();
    }
}
