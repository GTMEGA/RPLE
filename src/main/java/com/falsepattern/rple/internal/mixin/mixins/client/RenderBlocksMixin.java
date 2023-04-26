/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.Utils;
import lombok.val;
import lombok.var;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.renderer.RenderBlocks;

@Mixin(value = RenderBlocks.class,
       priority = 1001) //overwriting FalseTweaks mixAOBrightness
public abstract class RenderBlocksMixin {
    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public int getAoBrightness(int a, int b, int c, int d) {
        long packedA = Utils.cookieToPackedLong(a);
        long packedB = Utils.cookieToPackedLong(b);
        long packedC = Utils.cookieToPackedLong(c);
        long packedD = Utils.cookieToPackedLong(d);
        long resultPacked = 0;
        for (int i = 0; i <= 40; i += 8) {
            resultPacked |= getAOBrightnessChannel(packedA, packedB, packedC, packedD, i);
        }
        return Utils.packedLongToCookie(resultPacked);
    }

    private static long getAOBrightnessChannel(long packedA, long packedB, long packedC, long packedD, int channel) {
        int count = 0;
        float light = 0;
        var a = unit(packedA, channel);
        var b = unit(packedB, channel);
        var c = unit(packedC, channel);
        var d = unit(packedD, channel);
        if (a != 0) {
            light += a;
            count++;
        }
        if (b != 0) {
            light += b;
            count++;
        }
        if (c != 0) {
            light += c;
            count++;
        }
        if (d != 0) {
            light += d;
            count++;
        }
        light /= count;
        return (long)((int)light & 0xFF) << channel;
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public int mixAoBrightness(int a, int b, int c, int d, double aMul, double bMul, double cMul, double dMul) {
        long packedA = Utils.cookieToPackedLong(a);
        long packedB = Utils.cookieToPackedLong(b);
        long packedC = Utils.cookieToPackedLong(c);
        long packedD = Utils.cookieToPackedLong(d);
        long packedResult = 0;
        for (int i = 0; i <= 40; i += 8) {
            packedResult |= mixAoBrightnessChannel(packedA, packedB, packedC, packedD, aMul, bMul, cMul, dMul, i);
        }
        return Utils.packedLongToCookie(packedResult);
    }

    private static long mixAoBrightnessChannel(long a, long b, long c, long d, double aMul, double bMul, double cMul, double dMul, int channel) {
        val fA = unit(a, channel) * aMul;
        val fB = unit(b, channel) * bMul;
        val fC = unit(c, channel) * cMul;
        val fD = unit(d, channel) * dMul;
        return (long)((int)(fA + fB + fC + fD) & 0xFF) << channel;
    }

    private static int unit(long val, int channel) {
        return (int) ((val >>> channel) & 0xFF);
    }
}
