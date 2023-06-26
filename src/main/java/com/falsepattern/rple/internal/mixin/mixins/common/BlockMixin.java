/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common;

import com.falsepattern.rple.api.RPLEColorAPI;
import com.falsepattern.rple.api.block.ColoredLightBlock;
import com.falsepattern.rple.api.block.ColoredTranslucentBlock;
import com.falsepattern.rple.api.color.GreyscaleColor;
import com.falsepattern.rple.api.color.RPLEColor;
import com.falsepattern.rple.internal.mixin.interfaces.IBlockColorizerMixin;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Accessors(fluent = true, chain = false)
@Mixin(Block.class)
public abstract class BlockMixin implements IBlockColorizerMixin {
    @Shadow
    public abstract int getLightValue();

    @Shadow(remap = false)
    public abstract int getLightValue(IBlockAccess world, int x, int y, int z);

    @Shadow
    public abstract int getLightOpacity();

    @Shadow(remap = false)
    public abstract int getLightOpacity(IBlockAccess world, int x, int y, int z);

    @Setter
    @Nullable
    private RPLEColor baseColoredBrightness = null;
    @Setter
    @Nullable
    private RPLEColor baseColoredTranslucency = null;

    @Setter
    @Nullable
    private RPLEColor @Nullable [] metaColoredBrightness = null;
    @Setter
    @Nullable
    private RPLEColor @Nullable [] metaColoredTranslucency = null;

    private ThreadLocal<Boolean> passBaseBrightness = new ThreadLocal<>();
    private ThreadLocal<Boolean> passBaseOpacity = new ThreadLocal<>();

    @Inject(method = "<init>",
            at = @At("RETURN"),
            require = 1)
    private void initThreadLocals(Material material, CallbackInfo ci) {
        passBaseBrightness = ThreadLocal.withInitial(() -> false);
        passBaseOpacity = ThreadLocal.withInitial(() -> false);
    }

    @Inject(method = "getLightValue()I",
            at = @At("HEAD"),
            cancellable = true,
            require = 1)
    private void rpleLightValue(CallbackInfoReturnable<Integer> cir) {
        if (passBaseBrightness.get())
            return;

        val color = rple$getColoredBrightness();
        val lightValue = RPLEColorAPI.maxColorComponent(color);

        cir.setReturnValue(lightValue);
        cir.cancel();
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
        if (passBaseBrightness.get())
            return;

        val blockMeta = world.getBlockMetadata(posX, posY, posZ);
        val color = rple$getColoredBrightness(world, blockMeta, posX, posY, posZ);
        val lightValue = RPLEColorAPI.maxColorComponent(color);

        cir.setReturnValue(lightValue);
        cir.cancel();
    }

    @Inject(method = "getLightOpacity()I",
            at = @At("HEAD"),
            cancellable = true,
            require = 1)
    private void rpleLightOpacity(CallbackInfoReturnable<Integer> cir) {
        if (passBaseOpacity.get())
            return;

        val color = rple$getColoredTranslucency();
        val lightValue = RPLEColorAPI.invertColorComponent(RPLEColorAPI.minColorComponent(color));

        cir.setReturnValue(lightValue);
        cir.cancel();
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
        if (passBaseOpacity.get())
            return;

        val blockMeta = world.getBlockMetadata(posX, posY, posZ);
        val color = rple$getColoredTranslucency(world, blockMeta, posX, posY, posZ);
        val lightOpacity = RPLEColorAPI.invertColorComponent(RPLEColorAPI.maxColorComponent(color));

        cir.setReturnValue(lightOpacity);
        cir.cancel();
    }

    @Override
    public RPLEColor rple$getColoredBrightness(int blockMeta) {
        if (thiz() instanceof ColoredLightBlock) {
            val coloredLightBlock = (ColoredLightBlock) thiz();
            val color = coloredLightBlock.getColoredBrightness(blockMeta);
            if (color != null)
                return color;
        }

        val metaBrightness = lookupMetaBrightness(blockMeta);
        if (metaBrightness != null)
            return metaBrightness;

        if (baseColoredBrightness != null)
            return baseColoredBrightness;

        return fallbackBrightness();
    }

    @Override
    public RPLEColor rple$getColoredBrightness(IBlockAccess world, int blockMeta, int posX, int posY, int posZ) {
        if (thiz() instanceof ColoredLightBlock) {
            val coloredLightBlock = (ColoredLightBlock) thiz();
            val color = coloredLightBlock.getColoredBrightness(world, blockMeta, posX, posY, posZ);
            if (color != null)
                return color;
        }

        val metaBrightness = lookupMetaBrightness(blockMeta);
        if (metaBrightness != null)
            return metaBrightness;

        if (baseColoredBrightness != null)
            return baseColoredBrightness;

        return fallbackBrightness(world, posX, posY, posZ);
    }

    @Override
    public RPLEColor rple$getColoredTranslucency(int blockMeta) {
        if (thiz() instanceof ColoredTranslucentBlock) {
            val coloredTranslucentBlock = (ColoredTranslucentBlock) thiz();
            val color = coloredTranslucentBlock.getColoredTranslucency(blockMeta);
            if (color != null)
                return color;
        }

        val metaTranslucency = lookupMetaTranslucency(blockMeta);
        if (metaTranslucency != null)
            return metaTranslucency;

        if (baseColoredTranslucency != null)
            return baseColoredTranslucency;

        return fallbackTranslucency();
    }

    @Override
    public RPLEColor rple$getColoredTranslucency(IBlockAccess world, int blockMeta, int posX, int posY, int posZ) {
        if (thiz() instanceof ColoredTranslucentBlock) {
            val coloredTranslucentBlock = (ColoredTranslucentBlock) thiz();
            val color = coloredTranslucentBlock.getColoredTranslucency(world, blockMeta, posX, posY, posZ);
            if (color != null)
                return color;
        }

        val metaTranslucency = lookupMetaTranslucency(blockMeta);
        if (metaTranslucency != null)
            return metaTranslucency;

        if (baseColoredTranslucency != null)
            return baseColoredTranslucency;

        return fallbackTranslucency(world, posX, posY, posZ);
    }

    private @Nullable RPLEColor lookupMetaBrightness(int blockMeta) {
        if (blockMeta < 0)
            return null;
        if (metaColoredBrightness == null)
            return null;
        if (blockMeta >= metaColoredBrightness.length)
            return null;

        return metaColoredBrightness[blockMeta];
    }

    private @Nullable RPLEColor lookupMetaTranslucency(int blockMeta) {
        if (blockMeta < 0)
            return null;
        if (metaColoredTranslucency == null)
            return null;
        if (blockMeta >= metaColoredTranslucency.length)
            return null;

        return metaColoredTranslucency[blockMeta];
    }

    private RPLEColor fallbackBrightness() {
        passBaseBrightness.set(true);
        val color = GreyscaleColor.fromVanillaLightValue(getLightValue());
        passBaseBrightness.set(false);
        return color;
    }

    private RPLEColor fallbackBrightness(IBlockAccess world, int posX, int posY, int posZ) {
        passBaseBrightness.set(true);
        val color = GreyscaleColor.fromVanillaLightValue(getLightValue(world, posX, posY, posZ));
        passBaseBrightness.set(false);
        return color;
    }

    private RPLEColor fallbackTranslucency() {
        passBaseOpacity.set(true);
        val color = GreyscaleColor.fromVanillaLightOpacity(getLightOpacity());
        passBaseOpacity.set(false);
        return color;
    }

    private RPLEColor fallbackTranslucency(IBlockAccess world, int posX, int posY, int posZ) {
        passBaseOpacity.set(true);
        val color = GreyscaleColor.fromVanillaLightOpacity(getLightOpacity(world, posX, posY, posZ));
        passBaseOpacity.set(false);
        return color;
    }

    private Block thiz() {
        return (Block) (Object) this;
    }
}
