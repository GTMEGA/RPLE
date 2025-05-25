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

package com.falsepattern.rple.internal.mixin.mixins.client.hbm;

import com.falsepattern.rple.api.client.ClientColorHelper;
import com.hbm.render.block.ct.RenderBlocksCT;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import stubpackage.com.hbm.render.block.ct.RenderBlocksCT$VertInfo;

@Mixin(value = RenderBlocksCT.VertInfo.class,
       remap = false)
public abstract class VertInfoMixin {
    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @SuppressWarnings("OverwriteModifiers")
    @Overwrite
    public static RenderBlocksCT.VertInfo avg(RenderBlocksCT.VertInfo... infos) {
        float r = 0.0F;
        float g = 0.0F;
        float b = 0.0F;

        for (int i = 0; i < infos.length; i++) {
            RenderBlocksCT$VertInfo vert = (RenderBlocksCT$VertInfo) infos[i];
            assert vert != null;
            r += vert.red;
            g += vert.green;
            b += vert.blue;
        }
        if (infos.length > 0) {
            r /= (float) infos.length;
            g /= (float) infos.length;
            b /= (float) infos.length;
        }
        int l;
        switch (infos.length) {
            case 2:
                l = ClientColorHelper.cookieAverage(false,
                                                    ((RenderBlocksCT$VertInfo) infos[0]).brightness,
                                                    ((RenderBlocksCT$VertInfo) infos[1]).brightness);
                break;
            case 4:
                l = ClientColorHelper.cookieAverage(false,
                                                    ((RenderBlocksCT$VertInfo) infos[0]).brightness,
                                                    ((RenderBlocksCT$VertInfo) infos[1]).brightness,
                                                    ((RenderBlocksCT$VertInfo) infos[2]).brightness,
                                                    ((RenderBlocksCT$VertInfo) infos[3]).brightness);
                break;
            default:
                val values = new int[infos.length];
                for (int i = 0; i < values.length; i++) {
                    values[i] = ((RenderBlocksCT$VertInfo) infos[i]).brightness;
                }
                l = ClientColorHelper.cookieAverage(false, values);
                break;
        }
        return new RenderBlocksCT.VertInfo(r, g, b, l);
    }
}
