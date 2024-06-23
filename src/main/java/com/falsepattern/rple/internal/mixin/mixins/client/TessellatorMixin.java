/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
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

    private long rple$packedBrightness;

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
            rawBuffer[rawBufferIndex + VertexConstants.getRedIndexNoShader()] = ClientColorHelper.tessFromRGB64Red(rple$packedBrightness);
            rawBuffer[rawBufferIndex + VertexConstants.getGreenIndexNoShader()] = ClientColorHelper.tessFromRGB64Green(rple$packedBrightness);
            rawBuffer[rawBufferIndex + VertexConstants.getBlueIndexNoShader()] = ClientColorHelper.tessFromRGB64Blue(rple$packedBrightness);
        }
        return false;
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public void setBrightness(int brightness) {
        rple$setPackedBrightness(CookieMonster.RGB64FromCookie(brightness));
    }

    @Override
    public void rple$setPackedBrightness(long packedBrightness) {
        hasBrightness = true;
        rple$packedBrightness = packedBrightness;
    }

    @Override
    public long rple$getPackedBrightness() {
        return rple$packedBrightness;
    }
}
