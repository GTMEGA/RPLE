/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.lightmap;

//TODO: remove the default methods
public interface LightMapRegistry {
    void registerLightMapGenerator(LightMapGenerator generator, int priority);

    void registerLightMapBase(LightMapBase base, int priority);

    void registerBlockLightMapBase(BlockLightMapBase blockBase, int priority);

    void registerSkyLightMapBase(SkyLightMapBase skyBase, int priority);

    void registerLightMapMask(LightMapMask mask);

    void registerBlockLightMapMask(BlockLightMapMask blockMask);

    void registerSkyLightMapMask(SkyLightMapMask skyMask);
}
