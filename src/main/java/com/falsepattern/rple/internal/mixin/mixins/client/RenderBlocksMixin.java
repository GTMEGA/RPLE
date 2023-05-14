/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.api.ColoredBlock;
import com.falsepattern.rple.internal.color.BlockLightUtil;
import com.falsepattern.rple.internal.color.CookieManager;
import lombok.val;
import lombok.var;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

@Mixin(value = RenderBlocks.class,
       priority = 1001) //overwriting FalseTweaks mixAOBrightness
public abstract class RenderBlocksMixin {
    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public int getAoBrightness(int a, int b, int c, int d) {
        long packedA = CookieManager.cookieToPackedLong(a);
        long packedB = CookieManager.cookieToPackedLong(b);
        long packedC = CookieManager.cookieToPackedLong(c);
        long packedD = CookieManager.cookieToPackedLong(d);
        long resultPacked = 0;
        for (int i = 0; i <= 40; i += 8) {
            resultPacked |= getAOBrightnessChannel(packedA, packedB, packedC, packedD, i);
        }
        return CookieManager.packedLongToCookie(resultPacked);
    }

    //Ugly evil mixin-mixin hack
    private int meta;
    private static final String GMBFBO = "getMixedBrightnessForBlockOffset(IIILorg/joml/Vector3ic;ZLcom/falsepattern/falsetweaks/modules/triangulator/renderblocks/Facing$Direction;)I";
    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    @Redirect(method = GMBFBO,
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;",
                       ordinal = 0,
                       remap = true),
              remap = false,
              require = 1)
    private Block grabDefaultLightMeta(IBlockAccess instance, int x, int y, int z) {
        meta = instance.getBlockMetadata(x, y, z);
        return instance.getBlock(x, y, z);
    }
    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    @Redirect(method = GMBFBO,
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/block/Block;getLightValue(Lnet/minecraft/world/IBlockAccess;III)I",
                       remap = true),
              remap = false,
              require = 3)
    private int grabDefaultLight(Block instance, IBlockAccess access, int x, int y, int z) {
        return BlockLightUtil.getCompactRGBLightValue(access, (ColoredBlock) instance, meta, x, y, z);
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
        long packedA = CookieManager.cookieToPackedLong(a);
        long packedB = CookieManager.cookieToPackedLong(b);
        long packedC = CookieManager.cookieToPackedLong(c);
        long packedD = CookieManager.cookieToPackedLong(d);
        long packedResult = 0;
        for (int i = 0; i <= 40; i += 8) {
            packedResult |= mixAoBrightnessChannel(packedA, packedB, packedC, packedD, aMul, bMul, cMul, dMul, i);
        }
        return CookieManager.packedLongToCookie(packedResult);
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
