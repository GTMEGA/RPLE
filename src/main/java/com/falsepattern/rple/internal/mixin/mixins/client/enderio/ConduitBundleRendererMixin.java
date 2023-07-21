/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.enderio;

import com.falsepattern.rple.internal.mixin.helper.EnderIOHelper;
import com.falsepattern.rple.internal.mixin.hook.ColoredLightingHooks;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import lombok.val;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ConduitBundleRenderer.class)
public abstract class ConduitBundleRendererMixin extends TileEntitySpecialRenderer implements
                                                                                   ISimpleBlockRenderingHandler {
    @Redirect(method = {"renderWorldBlock", "renderTileEntityAt"},
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/world/World;getLightBrightnessForSkyBlocks(IIII)I"),
              require = 2)
    @SideOnly(Side.CLIENT)
    public int cacheTessellatorBrightness(World world, int posX, int posY, int posZ, int minBlockLight) {
        val tessellatorBrightness =
                ColoredLightingHooks.getRGBBrightnessForTessellator(world, posX, posY, posZ, minBlockLight);
        EnderIOHelper.cacheTessellatorBrightness(tessellatorBrightness);
        return tessellatorBrightness;
    }


    @Redirect(method = "renderConduits",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/renderer/Tessellator;setBrightness(I)V"),
              require = 1)
    @SideOnly(Side.CLIENT)
    public void cacheTessellatorBrightness(Tessellator instance, int oldTessellatorBrightness) {
        instance.setBrightness(EnderIOHelper.loadTessellatorBrightness());
    }
}
