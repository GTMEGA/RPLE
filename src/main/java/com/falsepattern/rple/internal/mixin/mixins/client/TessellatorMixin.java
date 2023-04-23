package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.Constants;
import com.falsepattern.rple.internal.LightMap;
import com.falsepattern.rple.internal.Utils;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;

import java.nio.ShortBuffer;

import static com.falsepattern.rple.internal.Utils.COOKIE_BIT;

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
    private boolean enable(Tessellator instance) {
        if (this.hasBrightness) {
            enableLightMapTexture(14, LightMap.textureUnitRed);
            enableLightMapTexture(16, LightMap.textureUnitGreen);
            enableLightMapTexture(18, LightMap.textureUnitBlue);
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
        if (this.hasBrightness) {
            disableLightMapTexture(LightMap.textureUnitRed);
            disableLightMapTexture(LightMap.textureUnitGreen);
            disableLightMapTexture(LightMap.textureUnitBlue);
        }
        return false;
    }

    @Redirect(method = "addVertex",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/client/renderer/Tessellator;hasBrightness:Z",
                       opcode = Opcodes.GETFIELD),
              require = 1)
    private boolean customColor(Tessellator instance) {
        if (this.hasBrightness) {
            rawBuffer[rawBufferIndex + 7] = Utils.getRedPair(brightness);
            rawBuffer[rawBufferIndex + 8] = Utils.getGreenPair(brightness);
            rawBuffer[rawBufferIndex + 9] = Utils.getBluePair(brightness);
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

    private static void enableLightMapTexture(int position, int unit) {
        OpenGlHelper.setClientActiveTexture(unit);
        shortBuffer.position(position);
        GL11.glTexCoordPointer(2, Constants.BYTES_PER_VERTEX_COLOR * 4, shortBuffer);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private static void disableLightMapTexture(int unit) {
        OpenGlHelper.setClientActiveTexture(unit);
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    @ModifyConstant(method = "addVertex",
                    constant = {@Constant(intValue = Constants.BYTES_PER_VERTEX_VANILLA), @Constant(intValue = 4 * Constants.BYTES_PER_VERTEX_VANILLA)},
                    require = 2)
    private int extendBytesAddVertex(int constant) {
        return Constants.extendBytesPerVertex(constant);
    }

    @ModifyConstant(method = "getVertexState",
                    constant = @Constant(intValue = Constants.BYTES_PER_VERTEX_VANILLA * 4),
                    require = 1)
    private int extendBytesGVS(int constant) {
        return Constants.extendBytesPerVertex(constant);
    }

    @ModifyConstant(method = "draw",
                    constant = {@Constant(intValue = Constants.BYTES_PER_VERTEX_VANILLA * 4),
                                @Constant(intValue = Constants.BYTES_PER_VERTEX_VANILLA)},
                    require = 8)
    private int extendBytesDraw(int constant) {
        return Constants.extendBytesPerVertex(constant);
    }
}
