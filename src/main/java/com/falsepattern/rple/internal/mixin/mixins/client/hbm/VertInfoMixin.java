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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stubpackage.com.hbm.render.block.ct.RenderBlocksCT$VertInfo;

@Mixin(value = RenderBlocksCT.VertInfo.class,
       remap = false)
public abstract class VertInfoMixin {
    @Inject(method = "avg([Lcom/hbm/render/block/ct/RenderBlocksCT$VertInfo;)Lcom/hbm/render/block/ct/RenderBlocksCT$VertInfo;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0,
            expect = 0)
    private static void colorizeAvg(RenderBlocksCT.VertInfo[] infos,
                                    CallbackInfoReturnable<RenderBlocksCT.VertInfo> cir) {
        val result = new RenderBlocksCT.VertInfo(0.0F, 0.0F, 0.0F, 0);
        rple$average((RenderBlocksCT$VertInfo) result, infos);
        cir.setReturnValue(result);
    }

    @Inject(method = "avg(Lcom/hbm/render/block/ct/RenderBlocksCT$VertInfo;[Lcom/hbm/render/block/ct/RenderBlocksCT$VertInfo;)Lcom/hbm/render/block/ct/RenderBlocksCT$VertInfo;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0,
            expect = 0)
    private static void colorizeAvgInto(RenderBlocksCT.VertInfo writeTo,
                                        RenderBlocksCT.VertInfo[] infos,
                                        CallbackInfoReturnable<RenderBlocksCT.VertInfo> cir) {
        rple$average((RenderBlocksCT$VertInfo) writeTo, infos);
        cir.setReturnValue(writeTo);
    }

    @Unique
    private static void rple$average(RenderBlocksCT$VertInfo out, RenderBlocksCT.VertInfo[] infos) {
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
        out.red = r;
        out.green = g;
        out.blue = b;
        out.brightness = l;
    }
}
