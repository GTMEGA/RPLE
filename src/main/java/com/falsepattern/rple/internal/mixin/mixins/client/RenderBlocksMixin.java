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
import com.falsepattern.rple.internal.color.BrightnessUtil;
import com.falsepattern.rple.internal.color.CookieMonster;
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
        val avgBuffer = new long[4];
        avgBuffer[0] = CookieMonster.cookieToPackedLong(a);
        avgBuffer[1] = CookieMonster.cookieToPackedLong(b);
        avgBuffer[2] = CookieMonster.cookieToPackedLong(c);
        avgBuffer[3] = CookieMonster.cookieToPackedLong(d);
        return CookieMonster.packedLongToCookie(BrightnessUtil.packedAverage(avgBuffer, 4, true));
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

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public int mixAoBrightness(int a, int b, int c, int d, double aMul, double bMul, double cMul, double dMul) {
        long packedA = CookieMonster.cookieToPackedLong(a);
        long packedB = CookieMonster.cookieToPackedLong(b);
        long packedC = CookieMonster.cookieToPackedLong(c);
        long packedD = CookieMonster.cookieToPackedLong(d);
        long packedResult = 0;
        for (int i = 0; i <= 40; i += 8) {
            packedResult |= mixAoBrightnessChannel(packedA, packedB, packedC, packedD, aMul, bMul, cMul, dMul, i);
        }
        return CookieMonster.packedLongToCookie(packedResult);
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
