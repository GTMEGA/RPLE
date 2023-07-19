/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.architecturecraft;

import com.falsepattern.lib.util.MathUtil;
import com.falsepattern.rple.internal.common.helper.BrightnessUtil;
import com.falsepattern.rple.internal.common.helper.CookieMonster;
import com.falsepattern.rple.internal.mixin.interfaces.architecturecraft.IVector3Mixin;
import gcewing.architecture.*;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = BaseWorldRenderTarget.class,
       remap = false)
public abstract class BaseWorldRenderTargetMixin extends BaseRenderTarget {

    @Shadow protected abstract void setLight(float v, int i);

    @Shadow protected Block block;

    @Shadow protected IBlockAccess world;

    @Shadow protected BlockPos blockPos;

    public BaseWorldRenderTargetMixin(double v, double v1, double v2, IIcon iIcon) {
        super(v, v1, v2, iIcon);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    protected void aoLightVertex(Vector3 rawPos) {
        val vertexPos = (IVector3Mixin) rawPos;
        val normal = (IVector3Mixin) this.normal;
        double bR = 0.0;
        double bG = 0.0;
        double bB = 0.0;
        double sR = 0.0;
        double sG = 0.0;
        double sB = 0.0;
        double ao = 0.0;
        double totalMultiplier = 0.0;
        double offsetX = vertexPos.x() + 0.5 * normal.x();
        double offsetY = vertexPos.y() + 0.5 * normal.y();
        double offsetZ = vertexPos.z() + 0.5 * normal.z();

        for(int X = -1; X <= 1; X += 2) {
            for(int Y = -1; Y <= 1; Y += 2) {
                for(int Z = -1; Z <= 1; Z += 2) {
                    int x = BaseUtils.ifloor(offsetX + 0.5 * (double)X);
                    int y = BaseUtils.ifloor(offsetY + 0.5 * (double)Y);
                    int z = BaseUtils.ifloor(offsetZ + 0.5 * (double)Z);
                    BlockPos pos = new BlockPos(x, y, z);
                    double dX = X < 0 ? (double)(x + 1) - (offsetX - 0.5) : offsetX + 0.5 - (double)x;
                    double dY = Y < 0 ? (double)(y + 1) - (offsetY - 0.5) : offsetY + 0.5 - (double)y;
                    double dZ = Z < 0 ? (double)(z + 1) - (offsetZ - 0.5) : offsetZ + 0.5 - (double)z;
                    double lightMultiplier = dX * dY * dZ;
                    if (lightMultiplier > 0.0) {
                        int brightnessCookie;
                        try {
                            brightnessCookie = this.block.getMixedBrightnessForBlock(this.world, pos.x, pos.y, pos.z);
                        } catch (RuntimeException var38) {
                            System.out.printf("BaseWorldRenderTarget.aoLightVertex: getMixedBrightnessForBlock(%s) with weight %s for block at %s: %s\n", pos, lightMultiplier, this.blockPos, var38);
                            System.out.printf("BaseWorldRenderTarget.aoLightVertex: v = %s n = %s\n", vertexPos, normal);
                            throw var38;
                        }

                        float aoRaw;
                        if (!pos.equals(this.blockPos)) {
                            aoRaw = this.world.getBlock(pos.x, pos.y, pos.z).getAmbientOcclusionLightValue();
                        } else {
                            aoRaw = 1.0F;
                        }

                        long brightness = CookieMonster.cookieToPackedLong(brightnessCookie);

                        if (brightness != 0) {
                            val r = BrightnessUtil.getBrightnessRed(brightness);
                            val g = BrightnessUtil.getBrightnessGreen(brightness);
                            val b = BrightnessUtil.getBrightnessBlue(brightness);
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

                        ao += lightMultiplier * (double)aoRaw;
                    }
                }
            }
        }

        int brightnessCookie;
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
            val packed = BrightnessUtil.packedBrightnessFromTessellatorBrightnessChannels(red, green, blue);
            brightnessCookie = CookieMonster.packedLongToCookie(packed);
        } else {
            brightnessCookie = this.block.getMixedBrightnessForBlock(this.world, this.blockPos.x, this.blockPos.y, this.blockPos.z);
        }

        this.setLight(this.shade * (float)ao, brightnessCookie);
    }

    private static double scalarBlock(int brightness) {
        return BrightnessUtil.getBlockLightChannelFromBrightnessRender(brightness) / 240.0;
    }

    private static double scalarSky(int brightness) {
        return BrightnessUtil.getSkyLightChannelFromBrightnessRender(brightness) / 240.0;
    }

    private static int scalarsToBrightness(double block, double sky) {
        block = MathUtil.clamp(block, 0, 1) * 240;
        sky = MathUtil.clamp(sky, 0, 1) * 240;
        val iBlock = (int)block;
        val iSky = (int)sky;
        return BrightnessUtil.channelsToBrightnessRender(iBlock, iSky);
    }
}
