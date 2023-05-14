/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.falsetweaks.api.triangulator.VertexAPI;
import com.falsepattern.rple.internal.Common;
import com.falsepattern.rple.internal.RPLE;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;

import com.falsepattern.rple.internal.color.BrightnessUtil;
import com.falsepattern.rple.internal.color.CookieManager;
import com.falsepattern.rple.internal.mixin.interfaces.ITessellatorJunction;
import lombok.val;
import org.lwjgl.opengl.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Tessellator.class)
public abstract class TessellatorMixin implements ITessellatorJunction {
    @Shadow private boolean hasBrightness;

    @Shadow private int[] rawBuffer;

    @Shadow private int rawBufferIndex;

    private long brightness;

    @Redirect(method = "draw",
              at = @At(value = "FIELD",
                     target = "Lnet/minecraft/client/renderer/Tessellator;hasBrightness:Z",
                     opcode = Opcodes.GETFIELD,
                     ordinal = 0),
              require = 1)
    private boolean enable(Tessellator tess) {
        if (hasBrightness) {
            enableLightMapTexture(tess, RPLE.getRedIndexNoShader() * 2, Common.RED_LIGHT_MAP_TEXTURE_UNIT);
            enableLightMapTexture(tess, RPLE.getGreenIndexNoShader() * 2, Common.GREEN_LIGHT_MAP_TEXTURE_UNIT);
            enableLightMapTexture(tess, RPLE.getBlueIndexNoShader() * 2, Common.BLUE_LIGHT_MAP_TEXTURE_UNIT);
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
            disableLightMapTexture(Common.RED_LIGHT_MAP_TEXTURE_UNIT);
            disableLightMapTexture(Common.GREEN_LIGHT_MAP_TEXTURE_UNIT);
            disableLightMapTexture(Common.BLUE_LIGHT_MAP_TEXTURE_UNIT);
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
            rawBuffer[rawBufferIndex + RPLE.getRedIndexNoShader()] = BrightnessUtil.getBrightnessRed(brightness);
            rawBuffer[rawBufferIndex + RPLE.getGreenIndexNoShader()] = BrightnessUtil.getBrightnessGreen(brightness);
            rawBuffer[rawBufferIndex + RPLE.getBlueIndexNoShader()] = BrightnessUtil.getBrightnessBlue(brightness);
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
        this.brightness = CookieManager.cookieToPackedLong(brightness);
    }

    private static void enableLightMapTexture(Tessellator tess, int position, int unit) {
        val shortBuffer = ((ITessellatorJunction)tess).RPLEgetShortBuffer();
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

    @Override
    public long brightness() {
        return brightness;
    }
}
