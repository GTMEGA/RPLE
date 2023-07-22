/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.client.lightmap;

import org.jetbrains.annotations.NotNull;

public interface RPLELightMapRegistry {
    void registerLightMapGenerator(@NotNull RPLELightMapGenerator generator, int priority);

    void registerLightMapBase(@NotNull RPLELightMapBase base, int priority);

    void registerBlockLightMapBase(@NotNull RPLEBlockLightMapBase blockBase, int priority);

    void registerSkyLightMapBase(@NotNull RPLESkyLightMapBase skyBase, int priority);

    void registerLightMapMask(@NotNull RPLELightMapMask mask);

    void registerBlockLightMapMask(@NotNull RPLEBlockLightMapMask blockMask);

    void registerSkyLightMapMask(@NotNull RPLESkyLightMapMask skyMask);
}
