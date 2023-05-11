/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.falsetweaks.api.triangulator.VertexAPI;
import com.falsepattern.rple.internal.LightMap;
import com.falsepattern.rple.internal.RPLE;
import com.falsepattern.rple.internal.Utils;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import java.nio.ShortBuffer;

@Mixin(Tessellator.class)
public abstract class TessellatorMixin {
    @Shadow private boolean hasBrightness;

    @Shadow private static ShortBuffer shortBuffer;

    @Shadow private int color;

    @Shadow private int[] rawBuffer;

    @Shadow private int rawBufferIndex;

    @Shadow private int rawBufferSize;

    private long brightness;

    @Redirect(method = "draw",
              at = @At(value = "FIELD",
                     target = "Lnet/minecraft/client/renderer/Tessellator;hasBrightness:Z",
                     opcode = Opcodes.GETFIELD,
                     ordinal = 0),
              require = 1)
    private boolean enable(Tessellator tess) {
        if (hasBrightness) {
            enableLightMapTexture(tess, RPLE.getRedIndexNoShader() * 2, LightMap.RED_LIGHT_MAP_TEXTURE_UNIT);
            enableLightMapTexture(tess, RPLE.getGreenIndexNoShader() * 2, LightMap.GREEN_LIGHT_MAP_TEXTURE_UNIT);
            enableLightMapTexture(tess, RPLE.getBlueIndexNoShader() * 2, LightMap.BLUE_LIGHT_MAP_TEXTURE_UNIT);
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
        if (hasBrightness) {
            disableLightMapTexture(LightMap.RED_LIGHT_MAP_TEXTURE_UNIT);
            disableLightMapTexture(LightMap.GREEN_LIGHT_MAP_TEXTURE_UNIT);
            disableLightMapTexture(LightMap.BLUE_LIGHT_MAP_TEXTURE_UNIT);
        }
        return false;
    }

    @Redirect(method = "addVertex",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/client/renderer/Tessellator;hasBrightness:Z",
                       opcode = Opcodes.GETFIELD),
              require = 1)
    private boolean customColor(Tessellator instance) {
        if (hasBrightness) {
            rawBuffer[rawBufferIndex + RPLE.getRedIndexNoShader()] = Utils.getRedPair(brightness);
            rawBuffer[rawBufferIndex + RPLE.getGreenIndexNoShader()] = Utils.getGreenPair(brightness);
            rawBuffer[rawBufferIndex + RPLE.getBlueIndexNoShader()] = Utils.getBluePair(brightness);
        }
        return false;
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public void setBrightness(int brightness) {
        this.hasBrightness = true;
        this.brightness = Utils.cookieToPackedLong(brightness);
    }

    private static void enableLightMapTexture(Tessellator tess, int position, int unit) {
        OpenGlHelper.setClientActiveTexture(unit);
        shortBuffer.position(position);
        GL11.glTexCoordPointer(2, VertexAPI.recomputeVertexInfo(8, 4), shortBuffer);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private static void disableLightMapTexture(int unit) {
        OpenGlHelper.setClientActiveTexture(unit);
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
