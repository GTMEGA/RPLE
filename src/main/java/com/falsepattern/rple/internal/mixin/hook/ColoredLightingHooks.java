/*
 * Copyright (c) 2023 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.falsepattern.rple.internal.mixin.hook;

import com.falsepattern.rple.api.RPLERenderAPI;
import com.falsepattern.rple.internal.common.helper.BrightnessUtil;
import com.falsepattern.rple.internal.common.helper.CookieMonster;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.world.World;

@UtilityClass
public final class ColoredLightingHooks {
    public static int getRGBBrightnessForTessellator(World world,
                                                     int posX,
                                                     int posY,
                                                     int posZ,
                                                     int cookieMinBlockLight) {
        val packedMinBrightness = CookieMonster.cookieToPackedLong(cookieMinBlockLight);

        val minRedBrightness = BrightnessUtil.getBrightnessRed(packedMinBrightness);
        val minGreenBrightness = BrightnessUtil.getBrightnessGreen(packedMinBrightness);
        val minBlueBrightness = BrightnessUtil.getBrightnessBlue(packedMinBrightness);

        val minRedBlockLight = BrightnessUtil.getBlockLightFromBrightness(minRedBrightness);
        val minGreenBlockLight = BrightnessUtil.getBlockLightFromBrightness(minGreenBrightness);
        val minBlueBlockLight = BrightnessUtil.getBlockLightFromBrightness(minBlueBrightness);

        return RPLERenderAPI.getRGBBrightnessForTessellator(world,
                                                            posX,
                                                            posY,
                                                            posZ,
                                                            minRedBlockLight,
                                                            minGreenBlockLight,
                                                            minBlueBlockLight);
    }
}
