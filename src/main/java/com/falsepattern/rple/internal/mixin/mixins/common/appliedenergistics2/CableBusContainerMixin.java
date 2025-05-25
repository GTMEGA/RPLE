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

package com.falsepattern.rple.internal.mixin.mixins.common.appliedenergistics2;

import appeng.api.util.AEColor;
import appeng.helpers.AEMultiTile;
import appeng.parts.CableBusContainer;
import appeng.parts.CableBusStorage;
import appeng.parts.ICableBusContainer;
import com.falsepattern.rple.api.common.ServerColorHelper;
import com.falsepattern.rple.internal.mixin.interfaces.appliedenergistics2.ICableBusContainerMixin;
import lombok.val;
import lombok.var;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static com.falsepattern.rple.api.common.color.LightValueColor.LIGHT_VALUE_0;

@Mixin(value = CableBusContainer.class,
       remap = false)
public abstract class CableBusContainerMixin extends CableBusStorage implements AEMultiTile,
                                                                                ICableBusContainer,
                                                                                ICableBusContainerMixin {
    @Shadow
    public abstract AEColor getColor();

    @Override
    public short rple$getColoredBrightness() {
        var light = 0;
        for (val direction : ForgeDirection.values()) {
            val part = getPart(direction);
            if (part != null)
                light = Math.max(part.getLightLevel(), light);
        }
        if (light < 1)
            return LIGHT_VALUE_0.rgb16();

        val colorRGB = getColor().mediumVariant;
        val red = (int) ((float) ((colorRGB >>> 16) & 0xFF) / 255F * (float) light) & 0xF;
        val green = (int) ((float) ((colorRGB >>> 8) & 0xFF) / 255F * (float) light) & 0xF;
        val blue = (int) ((float) (colorRGB & 0xFF) / 255F * (float) light) & 0xF;
        return ServerColorHelper.RGB16FromRGBChannel4Bit(red, green, blue);
    }
}
