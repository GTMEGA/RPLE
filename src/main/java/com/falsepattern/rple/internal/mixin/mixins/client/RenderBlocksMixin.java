/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.api.client.RPLEBlockBrightnessUtil;
import com.falsepattern.rple.internal.Compat;
import com.falsepattern.rple.internal.client.render.CookieMonsterHelper;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// TODO: [PRE_RELEASE] Fluid translucency tweaks belong in FalseTweaks
@Deprecated
@Mixin(value = RenderBlocks.class,
       priority = 1001) //overwriting FalseTweaks mixAOBrightness
public abstract class RenderBlocksMixin {
    @Shadow
    public IBlockAccess blockAccess;

    @Shadow
    public abstract float getLiquidHeight(int p_147729_1_, int p_147729_2_, int p_147729_3_, Material p_147729_4_);

    @Shadow
    public boolean renderAllFaces;

    @Shadow
    public abstract IIcon getBlockIconFromSideAndMetadata(Block p_147787_1_, int p_147787_2_, int p_147787_3_);

    @Shadow
    public abstract void renderFaceYNeg(Block p_147768_1_, double p_147768_2_, double p_147768_4_, double p_147768_6_, IIcon p_147768_8_);

    @Shadow
    public abstract IIcon getBlockIconFromSide(Block p_147777_1_, int p_147777_2_);

    @Shadow
    public double renderMinY;

    @Shadow
    public double renderMaxY;

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public int getAoBrightness(int a, int b, int c, int d) {
        return CookieMonsterHelper.average(true, a, b, c, d);
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
    private int grabDefaultLight(Block block, IBlockAccess world, int posX, int posY, int posZ) {
        return RPLEBlockBrightnessUtil.getBlockBrightnessForTessellator(world, block, meta, posX, posY, posZ);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public int mixAoBrightness(int a, int b, int c, int d, double aMul, double bMul, double cMul, double dMul) {
        return CookieMonsterHelper.mixAOBrightness(a, b, c, d, aMul, bMul, cMul, dMul);
    }

    private static final float alpha = 0.7f;

    /**
     * @author FalsePattern
     * @reason Water transparency buff
     */
    @Overwrite
    public boolean renderBlockLiquid(Block block, int x, int y, int z) {
        val tess = Compat.tessellator();
        val colorMul = block.colorMultiplier(this.blockAccess, x, y, z);
        val red = (float) (colorMul >> 16 & 255) / 255.0F;
        val green = (float) (colorMul >> 8 & 255) / 255.0F;
        val blue = (float) (colorMul & 255) / 255.0F;
        val isWater = block == Blocks.water || block == Blocks.flowing_water;
        val renderTop = block.shouldSideBeRendered(this.blockAccess, x, y + 1, z, 1);
        val renderBottom = block.shouldSideBeRendered(this.blockAccess, x, y - 1, z, 0);
        val sideRender =
                new boolean[]{block.shouldSideBeRendered(this.blockAccess, x, y, z - 1, 2),
                              block.shouldSideBeRendered(this.blockAccess, x, y, z + 1, 3),
                              block.shouldSideBeRendered(this.blockAccess, x - 1, y, z, 4),
                              block.shouldSideBeRendered(this.blockAccess, x + 1, y, z, 5)};

        if (!renderTop && !renderBottom && !sideRender[0] && !sideRender[1] && !sideRender[2] && !sideRender[3]) {
            return false;
        } else {
            boolean flag2 = false;
            val f3 = 0.5F;
            val f4 = 1F;
            val f5 = 0.8F;
            val f6 = 0.6F;
            val d0 = 0D;
            val d1 = 1D;
            Material material = block.getMaterial();
            val i1 = this.blockAccess.getBlockMetadata(x, y, z);
            double d2 = this.getLiquidHeight(x, y, z, material);
            double d3 = this.getLiquidHeight(x, y, z + 1, material);
            double d4 = this.getLiquidHeight(x + 1, y, z + 1, material);
            double d5 = this.getLiquidHeight(x + 1, y, z, material);
            val d6 = 0.0010000000474974513D;
            float f9;
            float f10;
            float f11;

            if (this.renderAllFaces || renderTop) {
                flag2 = true;
                IIcon iicon = this.getBlockIconFromSideAndMetadata(block, 1, i1);
                val f7 = (float) BlockLiquid.getFlowDirection(this.blockAccess, x, y, z, material);

                if (f7 > -999.0F) {
                    iicon = this.getBlockIconFromSideAndMetadata(block, 2, i1);
                }

                d2 -= d6;
                d3 -= d6;
                d4 -= d6;
                d5 -= d6;
                final double d7;
                final double d8;
                final double d10;
                final double d12;
                final double d14;
                final double d16;
                final double d18;
                final double d20;

                if (f7 < -999.0F) {
                    d7 = iicon.getInterpolatedU(0D);
                    d14 = iicon.getInterpolatedV(0D);
                    d8 = d7;
                    d16 = iicon.getInterpolatedV(16D);
                    d10 = iicon.getInterpolatedU(16D);
                    d18 = d16;
                    d12 = d10;
                    d20 = d14;
                } else {
                    f9 = MathHelper.sin(f7) * 0.25F;
                    f10 = MathHelper.cos(f7) * 0.25F;
                    d7 = iicon.getInterpolatedU(8F + (-f10 - f9) * 16F);
                    d14 = iicon.getInterpolatedV(8F + (-f10 + f9) * 16F);
                    d8 = iicon.getInterpolatedU(8F + (-f10 + f9) * 16F);
                    d16 = iicon.getInterpolatedV(8F + (f10 + f9) * 16F);
                    d10 = iicon.getInterpolatedU(8F + (f10 + f9) * 16F);
                    d18 = iicon.getInterpolatedV(8F + (f10 - f9) * 16F);
                    d12 = iicon.getInterpolatedU(8F + (f10 - f9) * 16F);
                    d20 = iicon.getInterpolatedV(8F + (-f10 - f9) * 16F);
                }

                tess.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, x, y, z));

                if (isWater && blockAccess.getBlock(x, y - 1, z) != Blocks.air) {
                    tess.setColorRGBA_F(f4 * red, f4 * green, f4 * blue, alpha);
                } else {
                    tess.setColorOpaque_F(f4 * red, f4 * green, f4 * blue);
                }
                tess.addVertexWithUV(x + 0D, (double) y + d2, z + 0D, d7, d14);
                tess.addVertexWithUV(x + 0D, (double) y + d3, z + 1D, d8, d16);
                tess.addVertexWithUV(x + 1D, (double) y + d4, z + 1D, d10, d18);
                tess.addVertexWithUV(x + 1D, (double) y + d5, z + 0D, d12, d20);
                tess.addVertexWithUV(x + 0D, (double) y + d2, z + 0D, d7, d14);
                tess.addVertexWithUV(x + 1D, (double) y + d5, z + 0D, d12, d20);
                tess.addVertexWithUV(x + 1D, (double) y + d4, z + 1D, d10, d18);
                tess.addVertexWithUV(x + 0D, (double) y + d3, z + 1D, d8, d16);
            }

            if (this.renderAllFaces || renderBottom) {
                tess.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z));
                if (isWater && blockAccess.getBlock(x, y - 1, z) != Blocks.air) {
                    tess.setColorRGBA_F(f3, f3, f3, alpha);
                } else {
                    tess.setColorOpaque_F(f3, f3, f3);
                }
                this.renderFaceYNeg(block, (double) x, (double) y + d6, (double) z, this.getBlockIconFromSide(block, 0));
                flag2 = true;
            }

            for (int k1 = 0; k1 < 4; ++k1) {
                int xSide = x;
                int zSide = z;

                if (k1 == 0) {
                    zSide = z - 1;
                }

                if (k1 == 1) {
                    ++zSide;
                }

                if (k1 == 2) {
                    xSide = x - 1;
                }

                if (k1 == 3) {
                    ++xSide;
                }

                IIcon iicon1 = this.getBlockIconFromSideAndMetadata(block, k1 + 2, i1);

                if (this.renderAllFaces || sideRender[k1]) {
                    double d9;
                    double d11;
                    double d13;
                    double d15;
                    double d17;
                    double d19;

                    if (k1 == 0) {
                        d9 = d2;
                        d11 = d5;
                        d13 = x;
                        d17 = x + 1;
                        d15 = (double) z + d6;
                        d19 = (double) z + d6;
                    } else if (k1 == 1) {
                        d9 = d4;
                        d11 = d3;
                        d13 = x + 1;
                        d17 = x;
                        d15 = (double) (z + 1) - d6;
                        d19 = (double) (z + 1) - d6;
                    } else if (k1 == 2) {
                        d9 = d3;
                        d11 = d2;
                        d13 = (double) x + d6;
                        d17 = (double) x + d6;
                        d15 = z + 1;
                        d19 = z;
                    } else {
                        d9 = d5;
                        d11 = d4;
                        d13 = (double) (x + 1) - d6;
                        d17 = (double) (x + 1) - d6;
                        d15 = z;
                        d19 = z + 1;
                    }

                    flag2 = true;
                    float f8 = iicon1.getInterpolatedU(0.0D);
                    f9 = iicon1.getInterpolatedU(8.0D);
                    f10 = iicon1.getInterpolatedV((1.0D - d9) * 16.0D * 0.5D);
                    f11 = iicon1.getInterpolatedV((1.0D - d11) * 16.0D * 0.5D);
                    float f12 = iicon1.getInterpolatedV(8.0D);
                    tess.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, xSide, y, zSide));
                    float f13 = 1.0F;
                    f13 *= k1 < 2 ? f5 : f6;
                    if (isWater && blockAccess.getBlock(xSide, y, zSide) != Blocks.air) {
                        tess.setColorRGBA_F(f4 * f13 * red, f4 * f13 * green, f4 * f13 * blue, alpha);
                    } else {
                        tess.setColorOpaque_F(f4 * f13 * red, f4 * f13 * green, f4 * f13 * blue);
                    }
                    tess.addVertexWithUV(d13, (double) y + d9, d15, (double) f8, (double) f10);
                    tess.addVertexWithUV(d17, (double) y + d11, d19, (double) f9, (double) f11);
                    tess.addVertexWithUV(d17, (double) (y + 0), d19, (double) f9, (double) f12);
                    tess.addVertexWithUV(d13, (double) (y + 0), d15, (double) f8, (double) f12);
                    tess.addVertexWithUV(d13, (double) (y + 0), d15, (double) f8, (double) f12);
                    tess.addVertexWithUV(d17, (double) (y + 0), d19, (double) f9, (double) f12);
                    tess.addVertexWithUV(d17, (double) y + d11, d19, (double) f9, (double) f11);
                    tess.addVertexWithUV(d13, (double) y + d9, d15, (double) f8, (double) f10);
                }
            }

            this.renderMinY = d0;
            this.renderMaxY = d1;
            return flag2;
        }
    }

}
