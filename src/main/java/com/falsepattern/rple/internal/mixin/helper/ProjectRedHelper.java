/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
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

package com.falsepattern.rple.internal.mixin.helper;

import com.falsepattern.rple.api.common.color.DefaultColor;
import com.falsepattern.rple.api.common.color.LightValueColor;
import lombok.experimental.UtilityClass;
import lombok.val;
import mrtjp.projectred.illumination.ILight;

@UtilityClass
public final class ProjectRedHelper {
    public static short getLightBrightnessColor(ILight light) {
        if (!light.isOn())
            return LightValueColor.LIGHT_VALUE_0.rgb16();
        val blockMeta = light.getColor();
        return DefaultColor.fromVanillaBlockMeta(blockMeta).rgb16();
    }
}
