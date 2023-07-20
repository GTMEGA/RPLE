/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.lightmap;

import org.jetbrains.annotations.NotNull;

public interface LightMapRegistry {
    void registerLightMapGenerator(@NotNull LightMapGenerator generator, int priority);

    void registerLightMapBase(@NotNull LightMapBase base, int priority);

    void registerBlockLightMapBase(@NotNull BlockLightMapBase blockBase, int priority);

    void registerSkyLightMapBase(@NotNull SkyLightMapBase skyBase, int priority);

    void registerLightMapMask(@NotNull LightMapMask mask);

    void registerBlockLightMapMask(@NotNull BlockLightMapMask blockMask);

    void registerSkyLightMapMask(@NotNull SkyLightMapMask skyMask);
}
