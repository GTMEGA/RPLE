/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.api.lightmap;

public interface LightMapPipelineRegistry {
    default void register(LightMapGenerator generator, int priority) {
        register((LightMapBase) generator, priority);
        register(generator);
    }

    default void register(LightMapBase generator, int priority) {
        register((BlockLightMapBase) generator, priority);
        register((SkyLightMapBase) generator, priority);
    }

    void register(BlockLightMapBase generator, int priority);

    void register(SkyLightMapBase generator, int priority);

    default void register(LightMapMask generator) {
        register((BlockLightMapMask) generator);
        register((SkyLightMapMask) generator);
    }

    void register(BlockLightMapMask generator);

    void register(SkyLightMapMask generator);
}
