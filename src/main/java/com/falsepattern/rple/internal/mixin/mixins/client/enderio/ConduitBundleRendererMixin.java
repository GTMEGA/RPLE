/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.enderio;

import com.falsepattern.rple.internal.mixin.extension.EnderIOConduitsBrightnessHolder;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import lombok.val;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ConduitBundleRenderer.class,
       remap = false)
public abstract class ConduitBundleRendererMixin extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {
    @Redirect(method = {"renderWorldBlock", "renderTileEntityAt"},
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/world/World;getLightBrightnessForSkyBlocks(IIII)I"),
              remap = true,
              require = 1)
    public int cacheBrightness(World world, int posX, int posY, int posZ, int min) {
        val brightness = world.getLightBrightnessForSkyBlocks(posX, posY, posZ, min);
        EnderIOConduitsBrightnessHolder.setCookieBrightness(brightness);
        return brightness;
    }

    @Redirect(method = "renderConduits",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/renderer/Tessellator;setBrightness(I)V"),
              remap = true,
              require = 1)
    public void cacheBrightness(Tessellator instance, int oldBrightness) {
        instance.setBrightness(EnderIOConduitsBrightnessHolder.getCookieBrightness());
    }
}
