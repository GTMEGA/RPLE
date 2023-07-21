/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common;

import com.falsepattern.rple.api.RPLEColorAPI;
import com.falsepattern.rple.api.block.RPLEColoredBlockBrightnessProvider;
import com.falsepattern.rple.api.block.RPLEColoredBlockRoot;
import com.falsepattern.rple.api.block.RPLEColoredBlockTranslucencyProvider;
import com.falsepattern.rple.api.color.LightValueColor;
import com.falsepattern.rple.api.color.RPLEColor;
import com.falsepattern.rple.internal.mixin.interfaces.RPLEBlockInit;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Unique
@Mixin(Block.class)
public abstract class BlockMixin implements RPLEBlockInit, RPLEColoredBlockRoot {
    @Shadow
    public abstract int getLightValue();

    @Shadow(remap = false)
    public abstract int getLightValue(IBlockAccess world, int posX, int posY, int posZ);

    @Shadow
    public abstract int getLightOpacity();

    @Shadow(remap = false)
    public abstract int getLightOpacity(IBlockAccess world, int posX, int posY, int posZ);

    @Nullable
    private RPLEColor rple$baseColoredBrightness = null;
    @Nullable
    private RPLEColor rple$baseColoredTranslucency = null;

    @Nullable
    private RPLEColor @Nullable [] rple$metaColoredBrightness = null;
    @Nullable
    private RPLEColor @Nullable [] rple$metaColoredTranslucency = null;

    private ThreadLocal<Boolean> rple$passBaseBrightness = new ThreadLocal<>();
    private ThreadLocal<Boolean> rple$passBaseOpacity = new ThreadLocal<>();

    @Inject(method = "<init>",
            at = @At("RETURN"),
            require = 1)
    private void initThreadLocals(Material material, CallbackInfo ci) {
        rple$passBaseBrightness = ThreadLocal.withInitial(() -> false);
        rple$passBaseOpacity = ThreadLocal.withInitial(() -> false);
    }

    @Inject(method = "getLightValue()I",
            at = @At("HEAD"),
            cancellable = true,
            require = 1)
    private void rpleLightValue(CallbackInfoReturnable<Integer> cir) {
        if (rple$passBaseBrightness.get())
            return;

        val color = rple$getColoredBrightness();
        val lightValue = RPLEColorAPI.maxColorComponent(color);
        cir.setReturnValue(lightValue);
    }

    @Inject(method = "getLightValue(Lnet/minecraft/world/IBlockAccess;III)I",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 1)
    private void rpleLightValue(IBlockAccess world,
                                int posX,
                                int posY,
                                int posZ,
                                CallbackInfoReturnable<Integer> cir) {
        if (rple$passBaseBrightness.get())
            return;

        val blockMeta = world.getBlockMetadata(posX, posY, posZ);
        val color = rple$getColoredBrightness(world, blockMeta, posX, posY, posZ);
        val lightValue = RPLEColorAPI.maxColorComponent(color);
        cir.setReturnValue(lightValue);
    }

    @Inject(method = "getLightOpacity()I",
            at = @At("HEAD"),
            cancellable = true,
            require = 1)
    private void rpleLightOpacity(CallbackInfoReturnable<Integer> cir) {
        if (rple$passBaseOpacity.get())
            return;

        val color = rple$getColoredTranslucency();
        val lightOpacity = RPLEColorAPI.invertColorComponent(RPLEColorAPI.maxColorComponent(color));
        cir.setReturnValue(lightOpacity);
    }

    @Inject(method = "getLightOpacity(Lnet/minecraft/world/IBlockAccess;III)I",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 1)
    private void rpleLightOpacity(IBlockAccess world,
                                  int posX,
                                  int posY,
                                  int posZ,
                                  CallbackInfoReturnable<Integer> cir) {
        if (rple$passBaseOpacity.get())
            return;

        val blockMeta = world.getBlockMetadata(posX, posY, posZ);
        val color = rple$getColoredTranslucency(world, blockMeta, posX, posY, posZ);
        val lightOpacity = RPLEColorAPI.invertColorComponent(RPLEColorAPI.maxColorComponent(color));
        cir.setReturnValue(lightOpacity);
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface", "ConstantValue"})
    public @NotNull RPLEColor rple$getColoredBrightness() {
        if (this instanceof RPLEColoredBlockBrightnessProvider) {
            val block = (RPLEColoredBlockBrightnessProvider) this;
            try {
                val customBrightness = block.rple$getColoredBrightness();
                if (customBrightness != null)
                    return customBrightness;
            } catch (Exception ignored) {
            }
        }
        return rple$getFallbackColoredBrightness();
    }

    @Override
    public @NotNull RPLEColor rple$getFallbackColoredBrightness() {
        return rple$getFallbackColoredBrightness(0);
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface", "ConstantValue"})
    public @NotNull RPLEColor rple$getColoredBrightness(int blockMeta) {
        if (this instanceof RPLEColoredBlockBrightnessProvider) {
            val block = (RPLEColoredBlockBrightnessProvider) this;
            try {
                val customBrightness = block.rple$getColoredBrightness(blockMeta);
                if (customBrightness != null)
                    return customBrightness;
            } catch (Exception ignored) {
            }
        }
        return rple$getFallbackColoredBrightness(blockMeta);
    }

    @Override
    public @NotNull RPLEColor rple$getFallbackColoredBrightness(int blockMeta) {
        metaCheck:
        {
            if (blockMeta < 0)
                break metaCheck;
            if (rple$metaColoredBrightness == null)
                break metaCheck;
            if (blockMeta >= rple$metaColoredBrightness.length)
                break metaCheck;
            val brightness = rple$metaColoredBrightness[blockMeta];
            if (brightness != null)
                return brightness;
        }
        if (rple$baseColoredBrightness != null)
            return rple$baseColoredBrightness;

        rple$passBaseBrightness.set(true);
        val brightness = LightValueColor.fromVanillaLightValue(getLightValue());
        rple$passBaseBrightness.set(false);
        return brightness;
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface", "ConstantValue"})
    public @NotNull RPLEColor rple$getColoredBrightness(@NotNull IBlockAccess world,
                                                        int blockMeta,
                                                        int posX,
                                                        int posY,
                                                        int posZ) {
        if (this instanceof RPLEColoredBlockBrightnessProvider) {
            val block = (RPLEColoredBlockBrightnessProvider) this;
            try {
                val customBrightness = block.rple$getColoredBrightness(world, blockMeta, posX, posY, posZ);
                if (customBrightness != null)
                    return customBrightness;
            } catch (Exception ignored) {
            }
        }
        return rple$getFallbackColoredBrightness(world, blockMeta, posX, posY, posZ);
    }

    @Override
    public @NotNull RPLEColor rple$getFallbackColoredBrightness(@NotNull IBlockAccess world,
                                                                int blockMeta,
                                                                int posX,
                                                                int posY,
                                                                int posZ) {
        metaCheck:
        {
            if (blockMeta < 0)
                break metaCheck;
            if (rple$metaColoredBrightness == null)
                break metaCheck;
            if (blockMeta >= rple$metaColoredBrightness.length)
                break metaCheck;
            val brightness = rple$metaColoredBrightness[blockMeta];
            if (brightness != null)
                return brightness;
        }
        if (rple$baseColoredBrightness != null)
            return rple$baseColoredBrightness;

        rple$passBaseBrightness.set(true);
        val brightness = LightValueColor.fromVanillaLightValue(getLightValue(world, posX, posY, posZ));
        rple$passBaseBrightness.set(false);
        return brightness;
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface", "ConstantValue"})
    public @NotNull RPLEColor rple$getColoredTranslucency() {
        if (this instanceof RPLEColoredBlockTranslucencyProvider) {
            val block = (RPLEColoredBlockTranslucencyProvider) this;
            try {
                val customTranslucency = block.rple$getColoredTranslucency();
                if (customTranslucency != null)
                    return customTranslucency;
            } catch (Exception ignored) {
            }
        }
        return rple$getFallbackColoredTranslucency();
    }

    @Override
    public @NotNull RPLEColor rple$getFallbackColoredTranslucency() {
        return rple$getFallbackColoredTranslucency(0);
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface", "ConstantValue"})
    public @NotNull RPLEColor rple$getColoredTranslucency(int blockMeta) {
        if (this instanceof RPLEColoredBlockTranslucencyProvider) {
            val block = (RPLEColoredBlockTranslucencyProvider) this;
            try {
                val customTranslucency = block.rple$getColoredTranslucency(blockMeta);
                if (customTranslucency != null)
                    return customTranslucency;
            } catch (Exception ignored) {
            }
        }
        return rple$getFallbackColoredTranslucency(blockMeta);
    }

    @Override
    public @NotNull RPLEColor rple$getFallbackColoredTranslucency(int blockMeta) {
        metaCheck:
        {
            if (blockMeta < 0)
                break metaCheck;
            if (rple$metaColoredTranslucency == null)
                break metaCheck;
            if (blockMeta >= rple$metaColoredTranslucency.length)
                break metaCheck;
            val translucency = rple$metaColoredTranslucency[blockMeta];
            if (translucency != null)
                return translucency;
        }
        if (rple$baseColoredTranslucency != null)
            return rple$baseColoredTranslucency;

        rple$passBaseOpacity.set(true);
        val translucency = LightValueColor.fromVanillaLightOpacity(getLightOpacity());
        rple$passBaseOpacity.set(false);
        return translucency;
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface", "ConstantValue"})
    public @NotNull RPLEColor rple$getColoredTranslucency(@NotNull IBlockAccess world,
                                                          int blockMeta,
                                                          int posX,
                                                          int posY,
                                                          int posZ) {
        if (this instanceof RPLEColoredBlockTranslucencyProvider) {
            val block = (RPLEColoredBlockTranslucencyProvider) this;
            try {
                val customTranslucency = block.rple$getColoredTranslucency(world, blockMeta, posX, posY, posZ);
                if (customTranslucency != null)
                    return customTranslucency;
            } catch (Exception ignored) {
            }
        }
        return rple$getFallbackColoredTranslucency(world, blockMeta, posX, posY, posZ);
    }

    @Override
    public @NotNull RPLEColor rple$getFallbackColoredTranslucency(@NotNull IBlockAccess world,
                                                                  int blockMeta,
                                                                  int posX,
                                                                  int posY,
                                                                  int posZ) {
        metaCheck:
        {
            if (blockMeta < 0)
                break metaCheck;
            if (rple$metaColoredTranslucency == null)
                break metaCheck;
            if (blockMeta >= rple$metaColoredTranslucency.length)
                break metaCheck;
            val translucency = rple$metaColoredTranslucency[blockMeta];
            if (translucency != null)
                return translucency;
        }
        if (rple$baseColoredTranslucency != null)
            return rple$baseColoredTranslucency;

        rple$passBaseOpacity.set(true);
        val translucency = LightValueColor.fromVanillaLightOpacity(getLightOpacity(world, posX, posY, posZ));
        rple$passBaseOpacity.set(false);
        return translucency;
    }

    @Override
    public void rple$initBaseColoredBrightness(@Nullable RPLEColor baseColoredBrightness) {
        this.rple$baseColoredBrightness = baseColoredBrightness;
    }

    @Override
    public void rple$initBaseColoredTranslucency(@Nullable RPLEColor baseColoredTranslucency) {
        this.rple$baseColoredTranslucency = baseColoredTranslucency;
    }

    @Override
    public void rple$initMetaColoredBrightness(@Nullable RPLEColor @Nullable [] metaColoredBrightness) {
        this.rple$metaColoredBrightness = metaColoredBrightness;
    }

    @Override
    public void rple$initMetaColoredTranslucency(@Nullable RPLEColor @Nullable [] metaColoredTranslucency) {
        this.rple$metaColoredTranslucency = metaColoredTranslucency;
    }
}
