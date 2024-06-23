/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.architecturecraft;

import com.falsepattern.lib.util.MathUtil;
import com.falsepattern.rple.api.client.CookieMonster;
import com.falsepattern.rple.api.client.RGB64Helper;
import com.falsepattern.rple.internal.mixin.interfaces.architecturecraft.IVector3Mixin;
import gcewing.architecture.*;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = BaseWorldRenderTarget.class, remap = false)
public abstract class BaseWorldRenderTargetMixin extends BaseRenderTarget {
    @Shadow
    protected IBlockAccess world;
    @Shadow
    protected BlockPos blockPos;
    @Shadow
    protected Block block;

    @Shadow
    protected abstract void setLight(float v, int i);

    public BaseWorldRenderTargetMixin(double posX, double posY, double posZ, IIcon iIcon) {
        super(posX, posY, posZ, iIcon);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    @SuppressWarnings("CastToIncompatibleInterface")
    protected void aoLightVertex(Vector3 rawPos) {
        val vertexPos = (IVector3Mixin) rawPos;
        val normal = (IVector3Mixin) this.normal;
        var bR = 0.0;
        var bG = 0.0;
        var bB = 0.0;
        var sR = 0.0;
        var sG = 0.0;
        var sB = 0.0;
        var ao = 0.0;
        var totalMultiplier = 0.0;
        val offsetX = vertexPos.rple$posX() + 0.5 * normal.rple$posX();
        val offsetY = vertexPos.rple$posY() + 0.5 * normal.rple$posY();
        val offsetZ = vertexPos.rple$posZ() + 0.5 * normal.rple$posZ();

        for (var X = -1; X <= 1; X += 2) {
            for (var Y = -1; Y <= 1; Y += 2) {
                for (var Z = -1; Z <= 1; Z += 2) {
                    val x = BaseUtils.ifloor(offsetX + 0.5 * (double) X);
                    val y = BaseUtils.ifloor(offsetY + 0.5 * (double) Y);
                    val z = BaseUtils.ifloor(offsetZ + 0.5 * (double) Z);
                    val pos = new BlockPos(x, y, z);
                    val dX = X < 0 ? (double) (x + 1) - (offsetX - 0.5) : offsetX + 0.5 - (double) x;
                    val dY = Y < 0 ? (double) (y + 1) - (offsetY - 0.5) : offsetY + 0.5 - (double) y;
                    val dZ = Z < 0 ? (double) (z + 1) - (offsetZ - 0.5) : offsetZ + 0.5 - (double) z;
                    val lightMultiplier = dX * dY * dZ;
                    if (lightMultiplier > 0.0) {
                        final int brightnessCookie;
                        try {
                            brightnessCookie = block.getMixedBrightnessForBlock(world, pos.x, pos.y, pos.z);
                        } catch (RuntimeException var38) {
                            System.out.printf("BaseWorldRenderTarget.aoLightVertex: getMixedBrightnessForBlock(%s) with weight %s for block at %s: %s\n", pos, lightMultiplier, this.blockPos, var38);
                            System.out.printf("BaseWorldRenderTarget.aoLightVertex: v = %s n = %s\n", vertexPos, normal);
                            throw var38;
                        }

                        final float aoRaw;
                        if (pos.equals(blockPos)) {
                            aoRaw = 1.0F;
                        } else {
                            aoRaw = world.getBlock(pos.x, pos.y, pos.z).getAmbientOcclusionLightValue();
                        }

                        val brightness = CookieMonster.cookieToPackedLong(brightnessCookie);

                        if (brightness != 0) {
                            val r = RGB64Helper.getBrightnessRed(brightness);
                            val g = RGB64Helper.getBrightnessGreen(brightness);
                            val b = RGB64Helper.getBrightnessBlue(brightness);
                            val rbR = scalarBlock(r);
                            val rbG = scalarBlock(g);
                            val rbB = scalarBlock(b);
                            val rsR = scalarSky(r);
                            val rsG = scalarSky(g);
                            val rsB = scalarSky(b);
                            bR += lightMultiplier * rbR;
                            bG += lightMultiplier * rbG;
                            bB += lightMultiplier * rbB;
                            sR += lightMultiplier * rsR;
                            sG += lightMultiplier * rsG;
                            sB += lightMultiplier * rsB;
                            totalMultiplier += lightMultiplier;
                        }

                        ao += lightMultiplier * (double) aoRaw;
                    }
                }
            }
        }

        final int brightnessCookie;
        if (totalMultiplier > 0.0) {
            val inverseMul = 1.0 / totalMultiplier;
            bR *= inverseMul;
            bG *= inverseMul;
            bB *= inverseMul;
            sR *= inverseMul;
            sG *= inverseMul;
            sB *= inverseMul;
            val red = scalarsToBrightness(bR, sR);
            val green = scalarsToBrightness(bG, sG);
            val blue = scalarsToBrightness(bB, sB);
            val packed = RGB64Helper.packedBrightnessFromTessellatorBrightnessChannels(red, green, blue);
            brightnessCookie = CookieMonster.packedLongToCookie(packed);
        } else {
            brightnessCookie = block.getMixedBrightnessForBlock(world, blockPos.x, blockPos.y, blockPos.z);
        }

        setLight(shade * (float) ao, brightnessCookie);
    }

    private static double scalarBlock(int brightness) {
        return RGB64Helper.getBlockLightChannelFromBrightnessRender(brightness) / 240.0;
    }

    private static double scalarSky(int brightness) {
        return RGB64Helper.getSkyLightChannelFromBrightnessRender(brightness) / 240.0;
    }

    private static int scalarsToBrightness(double block, double sky) {
        block = MathUtil.clamp(block, 0, 1) * 240;
        sky = MathUtil.clamp(sky, 0, 1) * 240;
        val iBlock = (int) block;
        val iSky = (int) sky;
        return RGB64Helper.channelsToBrightnessRender(iBlock, iSky);
    }
}
