/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.helper;

import com.falsepattern.rple.api.color.DefaultColor;
import com.falsepattern.rple.api.color.LightValueColor;
import com.falsepattern.rple.api.color.RPLEColor;
import lombok.experimental.UtilityClass;
import lombok.val;
import mrtjp.projectred.illumination.ILight;

@UtilityClass
public final class ProjectRedHelper {
    public static RPLEColor getLightBrightnessColor(ILight light) {
        if (!light.isOn())
            return LightValueColor.LIGHT_VALUE_0;
        val blockMeta = light.getColor();
        return DefaultColor.fromVanillaBlockMeta(blockMeta);
    }
}
