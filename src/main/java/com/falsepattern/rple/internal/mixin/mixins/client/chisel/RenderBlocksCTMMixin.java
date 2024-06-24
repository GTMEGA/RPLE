/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package com.falsepattern.rple.internal.mixin.mixins.client.chisel;

import com.falsepattern.rple.api.client.ClientColorHelper;
import net.minecraft.client.renderer.RenderBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import team.chisel.ctmlib.RenderBlocksCTM;

@Mixin(RenderBlocksCTM.class)
public abstract class RenderBlocksCTMMixin extends RenderBlocks {
    /**
     * @author Ven
     * @reason Colorize
     */
    @Overwrite(remap = false)
    @SuppressWarnings("OverwriteModifiers")
    private int avg(int... brightnessValues) {
        switch (brightnessValues.length) {
            case 2:
                return ClientColorHelper.cookieAverage(false, brightnessValues[0], brightnessValues[1]);
            case 4:
                return ClientColorHelper.cookieAverage(false,
                                            brightnessValues[0],
                                            brightnessValues[1],
                                            brightnessValues[2],
                                            brightnessValues[3]);
            default:
                return ClientColorHelper.cookieAverage(false, brightnessValues);
        }
    }
}
