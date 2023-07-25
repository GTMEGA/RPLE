package com.falsepattern.rple.internal.mixin.mixins.client.projectred;

import codechicken.lib.render.CCRenderState;
import mrtjp.projectred.illumination.LampTESR$;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LampTESR$.class)
public abstract class LampTESRMixin extends TileEntitySpecialRenderer implements IItemRenderer {
    @Inject(method = "render$1(DDDDLnet/minecraft/item/ItemStack;)V",
            at = @At(value = "INVOKE",
                     target = "codechicken/lib/render/CCRenderState.draw()V"),
            remap = false,
            require = 1)
    private void tryAndSee(double posX, double posY, double posZ, double scale, ItemStack itemStack, CallbackInfo ci) {
        CCRenderState.pushLightmap();
    }
}
