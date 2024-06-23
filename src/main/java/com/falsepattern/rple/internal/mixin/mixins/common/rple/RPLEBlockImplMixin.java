/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.rple;

import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.block.RPLEBlockRoot;
import com.falsepattern.rple.api.common.block.RPLECustomBlockBrightness;
import com.falsepattern.rple.api.common.block.RPLECustomBlockTranslucency;
import com.falsepattern.rple.api.common.color.RPLEColor;
import com.falsepattern.rple.internal.common.color.ColorPackingUtil;
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

    @Nullable
    @Dynamic("Initialized in: [com.falsepattern.rple.internal.mixin.mixins.common.rple.RPLEBlockInitImplMixin]")
    private RPLEColor rple$baseBrightnessColor;
    @Nullable
    @Dynamic("Initialized in: [com.falsepattern.rple.internal.mixin.mixins.common.rple.RPLEBlockInitImplMixin]")
    private RPLEColor rple$baseTranslucencyColor;
    @Nullable
    @Dynamic("Initialized in: [com.falsepattern.rple.internal.mixin.mixins.common.rple.RPLEBlockInitImplMixin]")
    private RPLEColor @Nullable [] rple$metaBrightnessColors;
    @Nullable
    @Dynamic("Initialized in: [com.falsepattern.rple.internal.mixin.mixins.common.rple.RPLEBlockInitImplMixin]")
    private RPLEColor @Nullable [] rple$metaTranslucencyColors;

    @Shadow
    public abstract boolean hasTileEntity(int metadata);

    @Override
    public short rple$getRawBrightnessColor() {
        if (rple$rawBaseBrightnessColor != -1)
            return rple$rawBaseBrightnessColor;
        if (this instanceof RPLECustomBlockBrightness) {
            val colorProvider = (RPLECustomBlockBrightness) this;
            try {
                val color = colorProvider.rple$getCustomBrightnessColor();
                return ColorPackingUtil.brightnessToCache(color);
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
                val color = colorProvider.rple$getCustomBrightnessColor(blockMeta);
                return ColorPackingUtil.brightnessToCache(color);
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
                val color = colorProvider.rple$getCustomBrightnessColor(world, blockMeta, posX, posY, posZ);
                return ColorPackingUtil.brightnessToCache(color);
            } catch (Exception ignored) {
            }
        }
        if (hasTileEntity(blockMeta)) {
            val tileEntity = world.getTileEntity(posX, posY, posZ);
            if (tileEntity instanceof RPLECustomBlockBrightness) {
                val colorProvider = (RPLECustomBlockBrightness) tileEntity;
                try {
                    val color = colorProvider.rple$getCustomBrightnessColor(world, blockMeta, posX, posY, posZ);
                    return ColorPackingUtil.brightnessToCache(color);
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
                val color = colorProvider.rple$getCustomTranslucencyColor();
                return ColorPackingUtil.translucencyToOpacityCache(color);
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
                return ColorPackingUtil.translucencyToOpacityCache(color);
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
                return  ColorPackingUtil.translucencyToOpacityCache(color);
            } catch (Exception ignored) {
            }
        }
        if (hasTileEntity(blockMeta)) {
            val tileEntity = world.getTileEntity(posX, posY, posZ);
            if (tileEntity instanceof RPLECustomBlockTranslucency) {
                val colorProvider = (RPLECustomBlockTranslucency) tileEntity;
                try {
                    val color = colorProvider.rple$getCustomTranslucencyColor(world, blockMeta, posX, posY, posZ);
                    return  ColorPackingUtil.translucencyToOpacityCache(color);
                } catch (Exception ignored) {
                }
            }
        }
        return rple$getRawInternalColoredOpacity(world, posX, posY, posZ);
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface", "ConstantValue"})
    public @NotNull RPLEColor rple$getBrightnessColor() {
        if (this instanceof RPLECustomBlockBrightness) {
            val colorProvider = (RPLECustomBlockBrightness) this;
            try {
                val color = colorProvider.rple$getCustomBrightnessColor();
                if (color != null)
                    return color;
            } catch (Exception ignored) {
            }
        }
        return rple$getFallbackBrightnessColor();
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface", "ConstantValue"})
    public @NotNull RPLEColor rple$getBrightnessColor(int blockMeta) {
        if (this instanceof RPLECustomBlockBrightness) {
            val colorProvider = (RPLECustomBlockBrightness) this;
            try {
                val color = colorProvider.rple$getCustomBrightnessColor(blockMeta);
                if (color != null)
                    return color;
            } catch (Exception ignored) {
            }
        }
        return rple$getFallbackBrightnessColor(blockMeta);
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface", "ConstantValue"})
    public @NotNull RPLEColor rple$getBrightnessColor(@NotNull IBlockAccess world,
                                                      int blockMeta,
                                                      int posX,
                                                      int posY,
                                                      int posZ) {
        if (this instanceof RPLECustomBlockBrightness) {
            val colorProvider = (RPLECustomBlockBrightness) this;
            try {
                val color = colorProvider.rple$getCustomBrightnessColor(world, blockMeta, posX, posY, posZ);
                if (color != null)
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
                    if (color != null)
                        return color;
                } catch (Exception ignored) {
                }
            }
        }
        return rple$getFallbackBrightnessColor(world, blockMeta, posX, posY, posZ);
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface", "ConstantValue"})
    public @NotNull RPLEColor rple$getTranslucencyColor() {
        if (this instanceof RPLECustomBlockTranslucency) {
            val colorProvider = (RPLECustomBlockTranslucency) this;
            try {
                val color = colorProvider.rple$getCustomTranslucencyColor();
                if (color != null)
                    return color;
            } catch (Exception ignored) {
            }
        }
        return rple$getFallbackTranslucencyColor();
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface", "ConstantValue"})
    public @NotNull RPLEColor rple$getTranslucencyColor(int blockMeta) {
        if (this instanceof RPLECustomBlockTranslucency) {
            val colorProvider = (RPLECustomBlockTranslucency) this;
            try {
                val color = colorProvider.rple$getCustomTranslucencyColor(blockMeta);
                if (color != null)
                    return color;
            } catch (Exception ignored) {
            }
        }
        return rple$getFallbackTranslucencyColor(blockMeta);
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface", "ConstantValue"})
    public @NotNull RPLEColor rple$getTranslucencyColor(@NotNull IBlockAccess world,
                                                        int blockMeta,
                                                        int posX,
                                                        int posY,
                                                        int posZ) {
        if (this instanceof RPLECustomBlockTranslucency) {
            val colorProvider = (RPLECustomBlockTranslucency) this;
            try {
                val color = colorProvider.rple$getCustomTranslucencyColor(world, blockMeta, posX, posY, posZ);
                if (color != null)
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
                    if (color != null)
                        return color;
                } catch (Exception ignored) {
                }
            }
        }
        return rple$getFallbackTranslucencyColor(world, blockMeta, posX, posY, posZ);
    }

    @Override
    public @NotNull RPLEColor rple$getFallbackBrightnessColor() {
        val color = rple$getConfiguredBrightnessColor();
        if (color != null)
            return color;
        return rple$getInternalColoredBrightness();
    }

    @Override
    public @NotNull RPLEColor rple$getFallbackBrightnessColor(int blockMeta) {
        val color = rple$getConfiguredBrightnessColor(blockMeta);
        if (color != null)
            return color;
        return rple$getInternalColoredBrightness();
    }

    @Override
    public @NotNull RPLEColor rple$getFallbackBrightnessColor(@NotNull IBlockAccess world, int blockMeta, int posX, int posY, int posZ) {
        val color = rple$getConfiguredBrightnessColor(blockMeta);
        if (color != null)
            return color;
        return rple$getInternalColoredBrightness(world, posX, posY, posZ);
    }

    @Override
    public @NotNull RPLEColor rple$getFallbackTranslucencyColor() {
        val color = rple$getConfiguredTranslucencyColor();
        if (color != null)
            return color;
        return rple$getInternalColoredTranslucency();
    }


    @Override
    public @NotNull RPLEColor rple$getFallbackTranslucencyColor(int blockMeta) {
        val color = rple$getConfiguredTranslucencyColor(blockMeta);
        if (color != null)
            return color;
        return rple$getInternalColoredTranslucency();
    }

    @Override
    public @NotNull RPLEColor rple$getFallbackTranslucencyColor(@NotNull IBlockAccess world,
                                                                int blockMeta,
                                                                int posX,
                                                                int posY,
                                                                int posZ) {
        val color = rple$getConfiguredTranslucencyColor(blockMeta);
        if (color != null)
            return color;
        return rple$getInternalColoredTranslucency(world, posX, posY, posZ);
    }

    @Override
    public @Nullable RPLEColor rple$getConfiguredBrightnessColor() {
        if (rple$baseBrightnessColor != null)
            return rple$baseBrightnessColor;
        if (rple$metaBrightnessColors == null)
            return null;
        if (rple$metaBrightnessColors.length < 1)
            return null;
        return rple$metaBrightnessColors[0];
    }

    @Override
    public @Nullable RPLEColor rple$getConfiguredBrightnessColor(int blockMeta) {
        if (rple$metaBrightnessColors == null)
            return rple$baseBrightnessColor;
        if (rple$metaBrightnessColors.length <= blockMeta)
            return rple$baseBrightnessColor;
        return rple$metaBrightnessColors[blockMeta];
    }

    @Override
    public @Nullable RPLEColor rple$getConfiguredTranslucencyColor() {
        if (rple$baseTranslucencyColor != null)
            return rple$baseTranslucencyColor;
        if (rple$metaTranslucencyColors == null)
            return null;
        if (rple$metaTranslucencyColors.length < 1)
            return null;
        return rple$metaTranslucencyColors[0];
    }

    @Override
    public @Nullable RPLEColor rple$getConfiguredTranslucencyColor(int blockMeta) {
        if (rple$metaTranslucencyColors == null)
            return rple$baseTranslucencyColor;
        if (rple$metaTranslucencyColors.length <= blockMeta)
            return rple$baseTranslucencyColor;
        return rple$metaTranslucencyColors[blockMeta];
    }
}
