/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common;

import com.falsepattern.rple.api.RPLEColorAPI;
import com.falsepattern.rple.api.block.RPLEBlock;
import com.falsepattern.rple.api.block.RPLEBlockBrightness;
import com.falsepattern.rple.api.block.RPLEBlockTranslucency;
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
public abstract class BlockMixin implements RPLEBlockInit, RPLEBlock {
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
        if (this instanceof RPLEBlockBrightness) {
            val block = (RPLEBlockBrightness) this;
            try {
                val customBrightness = block.getColoredBrightness();
                if (customBrightness != null)
                    return customBrightness;
            } catch (Exception ignored) {
            }
        }

        val metaBrightness = lookupMetaBrightness();
        if (metaBrightness != null)
            return metaBrightness;

        if (rple$baseColoredBrightness != null)
            return rple$baseColoredBrightness;

        return fallbackBrightness();
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface", "ConstantValue"})
    public @NotNull RPLEColor rple$getColoredBrightness(int blockMeta) {
        if (this instanceof RPLEBlockBrightness) {
            val block = (RPLEBlockBrightness) this;
            try {
                val customBrightness = block.getColoredBrightness(blockMeta);
                if (customBrightness != null)
                    return customBrightness;
            } catch (Exception ignored) {
            }
        }

        val metaBrightness = lookupMetaBrightness(blockMeta);
        if (metaBrightness != null)
            return metaBrightness;
        return fallbackBrightness();
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface", "ConstantValue"})
    public @NotNull RPLEColor rple$getColoredBrightness(@NotNull IBlockAccess world,
                                                        int blockMeta,
                                                        int posX,
                                                        int posY,
                                                        int posZ) {
        if (this instanceof RPLEBlockBrightness) {
            val block = (RPLEBlockBrightness) this;
            try {
                val customBrightness = block.getColoredBrightness(world, blockMeta, posX, posY, posZ);
                if (customBrightness != null)
                    return customBrightness;
            } catch (Exception ignored) {
            }
        }

        val metaBrightness = lookupMetaBrightness(blockMeta);
        if (metaBrightness != null)
            return metaBrightness;
        return fallbackBrightness(world, posX, posY, posZ);
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface", "ConstantValue"})
    public @NotNull RPLEColor rple$getColoredTranslucency() {
        if (this instanceof RPLEBlockTranslucency) {
            val block = (RPLEBlockTranslucency) this;
            try {
                val customTranslucency = block.getColoredTranslucency();
                if (customTranslucency != null)
                    return customTranslucency;
            } catch (Exception ignored) {
            }
        }

        val metaTranslucency = lookupMetaTranslucency();
        if (metaTranslucency != null)
            return metaTranslucency;

        if (rple$baseColoredTranslucency != null)
            return rple$baseColoredTranslucency;

        return fallbackTranslucency();
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface", "ConstantValue"})
    public @NotNull RPLEColor rple$getColoredTranslucency(int blockMeta) {
        if (this instanceof RPLEBlockTranslucency) {
            val block = (RPLEBlockTranslucency) this;
            try {
                val customTranslucency = block.getColoredTranslucency(blockMeta);
                if (customTranslucency != null)
                    return customTranslucency;
            } catch (Exception ignored) {
            }
        }

        val metaTranslucency = lookupMetaTranslucency(blockMeta);
        if (metaTranslucency != null)
            return metaTranslucency;

        if (rple$baseColoredTranslucency != null)
            return rple$baseColoredTranslucency;

        return fallbackTranslucency();
    }

    @Override
    @SuppressWarnings({"InstanceofThis", "InstanceofIncompatibleInterface", "ConstantValue"})
    public @NotNull RPLEColor rple$getColoredTranslucency(@NotNull IBlockAccess world,
                                                          int blockMeta,
                                                          int posX,
                                                          int posY,
                                                          int posZ) {
        if (this instanceof RPLEBlockTranslucency) {
            val block = (RPLEBlockTranslucency) this;
            try {
                val customTranslucency = block.getColoredTranslucency(world, blockMeta, posX, posY, posZ);
                if (customTranslucency != null)
                    return customTranslucency;
            } catch (Exception ignored) {
            }
        }

        val metaTranslucency = lookupMetaTranslucency(blockMeta);
        if (metaTranslucency != null)
            return metaTranslucency;

        if (rple$baseColoredTranslucency != null)
            return rple$baseColoredTranslucency;

        return fallbackTranslucency(world, posX, posY, posZ);
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

    private @Nullable RPLEColor lookupMetaBrightness() {
        return lookupMetaBrightness(0);
    }

    private @Nullable RPLEColor lookupMetaBrightness(int blockMeta) {
        if (blockMeta < 0)
            return rple$baseColoredBrightness;
        if (rple$metaColoredBrightness == null)
            return rple$baseColoredBrightness;
        if (blockMeta >= rple$metaColoredBrightness.length)
            return rple$baseColoredBrightness;
        val metaBrightness = rple$metaColoredBrightness[blockMeta];
        if (metaBrightness != null)
            return metaBrightness;
        return rple$baseColoredBrightness;
    }

    private @Nullable RPLEColor lookupMetaTranslucency() {
        return lookupMetaTranslucency(0);
    }

    private @Nullable RPLEColor lookupMetaTranslucency(int blockMeta) {
        if (blockMeta < 0)
            return null;
        if (rple$metaColoredTranslucency == null)
            return null;
        if (blockMeta >= rple$metaColoredTranslucency.length)
            return null;
        val metaTranslucency = rple$metaColoredTranslucency[blockMeta];
        if (metaTranslucency != null)
            return metaTranslucency;
        return rple$baseColoredTranslucency;
    }

    private RPLEColor fallbackBrightness() {
        rple$passBaseBrightness.set(true);
        val color = LightValueColor.fromVanillaLightValue(getLightValue());
        rple$passBaseBrightness.set(false);
        return color;
    }

    private RPLEColor fallbackBrightness(IBlockAccess world, int posX, int posY, int posZ) {
        rple$passBaseBrightness.set(true);
        val color = LightValueColor.fromVanillaLightValue(getLightValue(world, posX, posY, posZ));
        rple$passBaseBrightness.set(false);
        return color;
    }

    private RPLEColor fallbackTranslucency() {
        rple$passBaseOpacity.set(true);
        val color = LightValueColor.fromVanillaLightOpacity(getLightOpacity());
        rple$passBaseOpacity.set(false);
        return color;
    }

    private RPLEColor fallbackTranslucency(IBlockAccess world, int posX, int posY, int posZ) {
        rple$passBaseOpacity.set(true);
        val color = LightValueColor.fromVanillaLightOpacity(getLightOpacity(world, posX, posY, posZ));
        rple$passBaseOpacity.set(false);
        return color;
    }
}
