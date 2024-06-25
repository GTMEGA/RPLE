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

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.api.client.CookieMonster;
import com.falsepattern.rple.api.client.ClientColorHelper;
import com.falsepattern.rple.internal.client.lightmap.LightMap;
import com.falsepattern.rple.internal.client.render.VertexConstants;
import com.falsepattern.rple.internal.mixin.interfaces.ITessellatorMixin;
import lombok.val;
import net.minecraft.client.renderer.Tessellator;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(Tessellator.class)
public abstract class TessellatorMixin implements ITessellatorMixin {
    @Shadow
    private int[] rawBuffer;
    @Shadow
    private boolean hasBrightness;
    @Shadow
    private int rawBufferIndex;

    private long rple$rgb64;

    @Redirect(method = "draw",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/client/renderer/Tessellator;hasBrightness:Z",
                       opcode = Opcodes.GETFIELD,
                       ordinal = 0),
              require = 1)
    private boolean enable(Tessellator tess) {
        if (hasBrightness) {
            val shortBuffer = ((ITessellatorMixin) tess).rple$shortBuffer();
            LightMap.lightMap().prepare();
            LightMap.lightMap().enableVertexPointers(shortBuffer);
        }
        return false;
    }

    @Redirect(method = "draw",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/client/renderer/Tessellator;hasBrightness:Z",
                       opcode = Opcodes.GETFIELD,
                       ordinal = 1),
              require = 1)
    private boolean disable(Tessellator instance) {
        if (hasBrightness)
            LightMap.lightMap().disableVertexPointers();
        return false;
    }

    @Redirect(method = "addVertex",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/client/renderer/Tessellator;hasBrightness:Z",
                       opcode = Opcodes.GETFIELD),
              require = 1)
    private boolean customColor(Tessellator instance) {
        if (hasBrightness) {
            rawBuffer[rawBufferIndex + VertexConstants.getRedIndexNoShader()] = ClientColorHelper.tessFromRGB64Red(rple$rgb64);
            rawBuffer[rawBufferIndex + VertexConstants.getGreenIndexNoShader()] = ClientColorHelper.tessFromRGB64Green(rple$rgb64);
            rawBuffer[rawBufferIndex + VertexConstants.getBlueIndexNoShader()] = ClientColorHelper.tessFromRGB64Blue(rple$rgb64);
        }
        return false;
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public void setBrightness(int brightness) {
        rple$setRGB64Brightness(CookieMonster.RGB64FromCookie(brightness));
    }

    @Override
    public void rple$setRGB64Brightness(long rgb64) {
        hasBrightness = true;
        rple$rgb64 = rgb64;
    }

    @Override
    public long rple$getRGB64Brightness() {
        return rple$rgb64;
    }
}
