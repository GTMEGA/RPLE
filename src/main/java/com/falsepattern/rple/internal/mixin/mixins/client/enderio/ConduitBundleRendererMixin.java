package com.falsepattern.rple.internal.mixin.mixins.client.enderio;

import com.falsepattern.rple.internal.mixin.extension.EnderIOConduitsBrightnessHolder;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import lombok.*;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

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
