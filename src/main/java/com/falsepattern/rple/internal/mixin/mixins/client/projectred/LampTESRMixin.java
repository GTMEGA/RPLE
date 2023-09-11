package com.falsepattern.rple.internal.mixin.mixins.client.projectred;

import mrtjp.projectred.illumination.LampTESR$;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.IItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LampTESR$.class)
public abstract class LampTESRMixin extends TileEntitySpecialRenderer implements IItemRenderer {
    @Redirect(method = "render$1(DDDDLnet/minecraft/item/ItemStack;)V",
              at = @At(value = "INVOKE", target = "Lcodechicken/lib/render/CCRenderState;pullLightmap()V"),
              remap = false,
              require = 1)
    private void skipPullingLightCoords() {
    }
}
