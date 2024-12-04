package com.falsepattern.rple.internal.mixin.mixins.client.fairylights;

import com.pau101.fairylights.client.renderer.ConnectionRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(value = ConnectionRenderer.class,
       remap = false)
public abstract class ConnectionRendererMixin {
    @Redirect(method = "render",
              at = @At(value = "INVOKE",
                       target = "Ljava/util/List;iterator()Ljava/util/Iterator;"),
              require = 1)
    private Iterator<TileEntity> cmeSafe(List<TileEntity> instance) {
        return new ArrayList<>(instance).iterator();
    }
}
