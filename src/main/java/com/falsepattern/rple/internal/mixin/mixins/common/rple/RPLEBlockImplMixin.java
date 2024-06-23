/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.rple;

import com.falsepattern.rple.api.common.ServerColorHelper;
import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.block.RPLEBlockRoot;
import com.falsepattern.rple.api.common.block.RPLECustomBlockBrightness;
import com.falsepattern.rple.api.common.block.RPLECustomBlockTranslucency;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Unique
@Mixin(Block.class)
@SuppressWarnings("unused")
public abstract class RPLEBlockImplMixin implements RPLEBlock, RPLEBlockRoot {
    @Dynamic("Initialized in: [com.falsepattern.rple.internal.mixin.mixins.common.rple.RPLEBlockInitImplMixin]")
    private short rple$rawBaseBrightnessColor;
    @Dynamic("Initialized in: [com.falsepattern.rple.internal.mixin.mixins.common.rple.RPLEBlockInitImplMixin]")
    private short rple$rawBaseOpacityColor;
    @Dynamic("Initialized in: [com.falsepattern.rple.internal.mixin.mixins.common.rple.RPLEBlockInitImplMixin]")
    private short @Nullable [] rple$rawMetaBrightnessColors;
    @Dynamic("Initialized in: [com.falsepattern.rple.internal.mixin.mixins.common.rple.RPLEBlockInitImplMixin]")
    private short @Nullable [] rple$rawMetaOpacityColors;

    @Shadow
    public abstract boolean hasTileEntity(int metadata);

    @Override
    public short rple$getRawBrightnessColor() {
        if (rple$rawBaseBrightnessColor != -1)
            return rple$rawBaseBrightnessColor;
        if (this instanceof RPLECustomBlockBrightness) {
            val colorProvider = (RPLECustomBlockBrightness) this;
            try {
                return colorProvider.rple$getCustomBrightnessColor();
            } catch (Exception ignored) {
            }
        }
        return rple$getRawInternalColoredBrightness();
    }

    @Override
    public short rple$getRawBrightnessColor(int blockMeta) {
        checkMeta:
        {
            if (rple$rawMetaBrightnessColors == null)
                break checkMeta;
            if (blockMeta < 0 || blockMeta >= rple$rawMetaBrightnessColors.length)
                break checkMeta;
            val brightness = rple$rawMetaBrightnessColors[blockMeta];
            if (brightness != -1)
                return brightness;
        }
        if (rple$rawBaseBrightnessColor != -1)
            return rple$rawBaseBrightnessColor;

        if (this instanceof RPLECustomBlockBrightness) {
            val colorProvider = (RPLECustomBlockBrightness) this;
            try {
                return colorProvider.rple$getCustomBrightnessColor(blockMeta);
            } catch (Exception ignored) {
            }
        }
        return rple$getRawInternalColoredBrightness();
    }

    @Override
    public short rple$getRawBrightnessColor(@NotNull IBlockAccess world, int blockMeta, int posX, int posY, int posZ) {
        checkMeta:
        {
            if (rple$rawMetaBrightnessColors == null)
                break checkMeta;
            if (blockMeta < 0 || blockMeta >= rple$rawMetaBrightnessColors.length)
                break checkMeta;
            val brightness = rple$rawMetaBrightnessColors[blockMeta];
            if (brightness != -1)
                return brightness;
        }
        if (rple$rawBaseBrightnessColor != -1)
            return rple$rawBaseBrightnessColor;
        if (this instanceof RPLECustomBlockBrightness) {
            val colorProvider = (RPLECustomBlockBrightness) this;
            try {
                return colorProvider.rple$getCustomBrightnessColor(world, blockMeta, posX, posY, posZ);
            } catch (Exception ignored) {
            }
        }
        if (hasTileEntity(blockMeta)) {
            val tileEntity = world.getTileEntity(posX, posY, posZ);
            if (tileEntity instanceof RPLECustomBlockBrightness) {
                val colorProvider = (RPLECustomBlockBrightness) tileEntity;
                try {
                    return colorProvider.rple$getCustomBrightnessColor(world, blockMeta, posX, posY, posZ);
                } catch (Exception ignored) {
                }
            }
        }
        return rple$getRawInternalColoredBrightness(world, posX, posY, posZ);
    }

    @Override
    public short rple$getRawOpacityColor() {
        if (rple$rawBaseOpacityColor != -1)
            return rple$rawBaseOpacityColor;
        if (this instanceof RPLECustomBlockTranslucency) {
            val colorProvider = (RPLECustomBlockTranslucency) this;
            try {
                return colorProvider.rple$getCustomTranslucencyColor();
            } catch (Exception ignored) {
            }
        }
        return rple$getRawInternalColoredOpacity();
    }

    @Override
    public short rple$getRawOpacityColor(int blockMeta) {
        checkMeta:
        {
            if (rple$rawMetaOpacityColors == null)
                break checkMeta;
            if (blockMeta < 0 || blockMeta >= rple$rawMetaOpacityColors.length)
                break checkMeta;
            val opacity = rple$rawMetaOpacityColors[blockMeta];
            if (opacity != -1)
                return opacity;
        }
        if (rple$rawBaseOpacityColor != -1)
            return rple$rawBaseOpacityColor;
        if (this instanceof RPLECustomBlockTranslucency) {
            val colorProvider = (RPLECustomBlockTranslucency) this;
            try {
                val color = colorProvider.rple$getCustomTranslucencyColor(blockMeta);
                return ServerColorHelper.RGB16OpacityTranslucentSwap(color);
            } catch (Exception ignored) {
            }
        }
        return rple$getRawInternalColoredOpacity();
    }

    @Override
    public short rple$getRawOpacityColor(@NotNull IBlockAccess world, int blockMeta, int posX, int posY, int posZ) {
        checkMeta:
        {
            if (rple$rawMetaOpacityColors == null)
                break checkMeta;
            if (blockMeta < 0 || blockMeta >= rple$rawMetaOpacityColors.length)
                break checkMeta;
            val opacity = rple$rawMetaOpacityColors[blockMeta];
            if (opacity != -1)
                return opacity;
        }
        if (rple$rawBaseOpacityColor != -1)
            return rple$rawBaseOpacityColor;
        if (this instanceof RPLECustomBlockTranslucency) {
            val colorProvider = (RPLECustomBlockTranslucency) this;
            try {
                val color = colorProvider.rple$getCustomTranslucencyColor(world, blockMeta, posX, posY, posZ);
                return ServerColorHelper.RGB16OpacityTranslucentSwap(color);
            } catch (Exception ignored) {
            }
        }
        if (hasTileEntity(blockMeta)) {
            val tileEntity = world.getTileEntity(posX, posY, posZ);
            if (tileEntity instanceof RPLECustomBlockTranslucency) {
                val colorProvider = (RPLECustomBlockTranslucency) tileEntity;
                try {
                    val color = colorProvider.rple$getCustomTranslucencyColor(world, blockMeta, posX, posY, posZ);
                    return ServerColorHelper.RGB16OpacityTranslucentSwap(color);
                } catch (Exception ignored) {
                }
            }
        }
        return rple$getRawInternalColoredOpacity(world, posX, posY, posZ);
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface"})
    public short rple$getBrightnessColor() {
        if (this instanceof RPLECustomBlockBrightness) {
            val colorProvider = (RPLECustomBlockBrightness) this;
            try {
                val color = colorProvider.rple$getCustomBrightnessColor();
                if (color != -1)
                    return color;
            } catch (Exception ignored) {
            }
        }
        return rple$getFallbackBrightnessColor();
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface"})
    public short rple$getBrightnessColor(int blockMeta) {
        if (this instanceof RPLECustomBlockBrightness) {
            val colorProvider = (RPLECustomBlockBrightness) this;
            try {
                val color = colorProvider.rple$getCustomBrightnessColor(blockMeta);
                if (color != -1)
                    return color;
            } catch (Exception ignored) {
            }
        }
        return rple$getFallbackBrightnessColor(blockMeta);
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface"})
    public short rple$getBrightnessColor(@NotNull IBlockAccess world,
                                                      int blockMeta,
                                                      int posX,
                                                      int posY,
                                                      int posZ) {
        if (this instanceof RPLECustomBlockBrightness) {
            val colorProvider = (RPLECustomBlockBrightness) this;
            try {
                val color = colorProvider.rple$getCustomBrightnessColor(world, blockMeta, posX, posY, posZ);
                if (color != -1)
                    return color;
            } catch (Exception ignored) {
            }
        }
        if (hasTileEntity(blockMeta)) {
            val tileEntity = world.getTileEntity(posX, posY, posZ);
            if (tileEntity instanceof RPLECustomBlockBrightness) {
                val colorProvider = (RPLECustomBlockBrightness) tileEntity;
                try {
                    val color = colorProvider.rple$getCustomBrightnessColor(world, blockMeta, posX, posY, posZ);
                    if (color != -1)
                        return color;
                } catch (Exception ignored) {
                }
            }
        }
        return rple$getFallbackBrightnessColor(world, blockMeta, posX, posY, posZ);
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface"})
    public short rple$getTranslucencyColor() {
        if (this instanceof RPLECustomBlockTranslucency) {
            val colorProvider = (RPLECustomBlockTranslucency) this;
            try {
                val color = colorProvider.rple$getCustomTranslucencyColor();
                if (color != -1)
                    return color;
            } catch (Exception ignored) {
            }
        }
        return rple$getFallbackTranslucencyColor();
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface"})
    public short rple$getTranslucencyColor(int blockMeta) {
        if (this instanceof RPLECustomBlockTranslucency) {
            val colorProvider = (RPLECustomBlockTranslucency) this;
            try {
                val color = colorProvider.rple$getCustomTranslucencyColor(blockMeta);
                if (color != -1)
                    return color;
            } catch (Exception ignored) {
            }
        }
        return rple$getFallbackTranslucencyColor(blockMeta);
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface"})
    public short rple$getTranslucencyColor(@NotNull IBlockAccess world,
                                                        int blockMeta,
                                                        int posX,
                                                        int posY,
                                                        int posZ) {
        if (this instanceof RPLECustomBlockTranslucency) {
            val colorProvider = (RPLECustomBlockTranslucency) this;
            try {
                val color = colorProvider.rple$getCustomTranslucencyColor(world, blockMeta, posX, posY, posZ);
                if (color != -1)
                    return color;
            } catch (Exception ignored) {
            }
        }
        if (hasTileEntity(blockMeta)) {
            val tileEntity = world.getTileEntity(posX, posY, posZ);
            if (tileEntity instanceof RPLECustomBlockTranslucency) {
                val colorProvider = (RPLECustomBlockTranslucency) tileEntity;
                try {
                    val color = colorProvider.rple$getCustomTranslucencyColor(world, blockMeta, posX, posY, posZ);
                    if (color != -1)
                        return color;
                } catch (Exception ignored) {
                }
            }
        }
        return rple$getFallbackTranslucencyColor(world, blockMeta, posX, posY, posZ);
    }

    @Override
    public short rple$getFallbackBrightnessColor() {
        val color = rple$getConfiguredBrightnessColor();
        if (color != -1)
            return color;
        return rple$getInternalColoredBrightness();
    }

    @Override
    public short rple$getFallbackBrightnessColor(int blockMeta) {
        val color = rple$getConfiguredBrightnessColor(blockMeta);
        if (color != -1)
            return color;
        return rple$getInternalColoredBrightness();
    }

    @Override
    public short rple$getFallbackBrightnessColor(@NotNull IBlockAccess world, int blockMeta, int posX, int posY, int posZ) {
        val color = rple$getConfiguredBrightnessColor(blockMeta);
        if (color != -1)
            return color;
        return rple$getInternalColoredBrightness(world, posX, posY, posZ);
    }

    @Override
    public short rple$getFallbackTranslucencyColor() {
        val color = rple$getConfiguredTranslucencyColor();
        if (color != -1)
            return color;
        return rple$getInternalColoredTranslucency();
    }


    @Override
    public short rple$getFallbackTranslucencyColor(int blockMeta) {
        val color = rple$getConfiguredTranslucencyColor(blockMeta);
        if (color != -1)
            return color;
        return rple$getInternalColoredTranslucency();
    }

    @Override
    public short rple$getFallbackTranslucencyColor(@NotNull IBlockAccess world,
                                                                int blockMeta,
                                                                int posX,
                                                                int posY,
                                                                int posZ) {
        val color = rple$getConfiguredTranslucencyColor(blockMeta);
        if (color != -1)
            return color;
        return rple$getInternalColoredTranslucency(world, posX, posY, posZ);
    }

    @Override
    public short rple$getConfiguredBrightnessColor() {
        if (rple$rawBaseBrightnessColor != -1)
            return rple$rawBaseBrightnessColor;
        if (rple$rawMetaBrightnessColors == null)
            return -1;
        if (rple$rawMetaBrightnessColors.length < 1)
            return -1;
        return rple$rawMetaBrightnessColors[0];
    }

    @Override
    public short rple$getConfiguredBrightnessColor(int blockMeta) {
        if (rple$rawMetaBrightnessColors == null)
            return rple$rawBaseBrightnessColor;
        if (rple$rawMetaBrightnessColors.length <= blockMeta)
            return rple$rawBaseBrightnessColor;
        return rple$rawMetaBrightnessColors[blockMeta];
    }

    @Override
    public short rple$getConfiguredTranslucencyColor() {
        if (rple$rawBaseOpacityColor != -1)
            return ServerColorHelper.RGB16OpacityTranslucentSwap(rple$rawBaseOpacityColor);
        if (rple$rawMetaOpacityColors == null)
            return -1;
        if (rple$rawMetaOpacityColors.length < 1)
            return -1;
        return ServerColorHelper.RGB16OpacityTranslucentSwap(rple$rawMetaOpacityColors[0]);
    }

    @Override
    public short rple$getConfiguredTranslucencyColor(int blockMeta) {
        if (rple$rawMetaOpacityColors == null)
            return ServerColorHelper.RGB16OpacityTranslucentSwap(rple$rawBaseOpacityColor);
        if (rple$rawMetaOpacityColors.length <= blockMeta)
            return ServerColorHelper.RGB16OpacityTranslucentSwap(rple$rawBaseOpacityColor);
        return ServerColorHelper.RGB16OpacityTranslucentSwap(rple$rawMetaOpacityColors[blockMeta]);
    }
}
