package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.Utils;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.renderer.RenderBlocks;

import static com.falsepattern.rple.internal.Utils.MASK;

@Mixin(RenderBlocks.class)
public abstract class RenderBlocksMixin {
    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public int getAoBrightness(int a, int b, int c, int def) {
        int result = MASK;
        for (int i = 0; i <= 25; i += 5) {
            result |= getAOBrightnessChannel(a, b, c, def, i) << i;
        }
        return result;
    }

    private static int getAOBrightnessChannel(int a, int b, int c, int d, int channel) {
        a = unit(a, channel);
        b = unit(b, channel);
        c = unit(c, channel);
        d = unit(d, channel);
        if (a < d) {
            a = d;
        }
        if (b < d) {
            b = d;
        }
        if (c < d) {
            c = d;
        }
        return ((a + b + c + d) >>> 2) & 0x1F;
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public int mixAoBrightness(int a, int b, int c, int d, double aMul, double bMul, double cMul, double dMul) {
        int result = MASK;
        for (int i = 0; i <= 25; i += 5) {
            result |= mixAoBrightnessChannel(a, b, c, d, aMul, bMul, cMul, dMul, i) << i;
        }
        return result;
    }

    @ModifyConstant(method = {"renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithAmbientOcclusionPartial"},
                    constant = @Constant(intValue = 0xf000f),
                    require = 2)
    private int replaceSetBrightness(int constant) {
        return Utils.MIN;
    }

    private static int mixAoBrightnessChannel(int a, int b, int c, int d, double aMul, double bMul, double cMul, double dMul, int channel) {
        val fA = unit(a, channel) * aMul;
        val fB = unit(b, channel) * bMul;
        val fC = unit(c, channel) * cMul;
        val fD = unit(d, channel) * dMul;
        return (int)(fA + fB + fC + fD) & 0x1F;
    }

    private static int unit(int val, int channel) {
        return (val >>> channel) & 0x1F;
    }
}
