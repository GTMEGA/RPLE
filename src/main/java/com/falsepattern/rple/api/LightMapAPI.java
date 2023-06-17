/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.api;

import com.falsepattern.rple.api.lightmap.LightMapPipelineRegistry;
import com.falsepattern.rple.internal.lightmap.LightMapPipeline;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class LightMapAPI {
    public static LightMapPipelineRegistry lightMapPipelineRegistry() {
        return LightMapPipeline.lightMapPipeline();
    }
}
