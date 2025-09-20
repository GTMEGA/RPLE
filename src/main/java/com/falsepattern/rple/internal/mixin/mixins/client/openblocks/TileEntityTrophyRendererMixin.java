package com.falsepattern.rple.internal.mixin.mixins.client.openblocks;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import openblocks.client.renderer.tileentity.TileEntityTrophyRenderer;
import openblocks.common.TrophyHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TileEntityTrophyRenderer.class)
public abstract class TileEntityTrophyRendererMixin {
    @Unique
    private static final ThreadLocal<Boolean> rple$renderingInWorld = ThreadLocal.withInitial(() -> false);

    @WrapOperation(method = "renderTileEntityAt",
                   at = @At(value = "INVOKE", target = "Lopenblocks/client/renderer/tileentity/TileEntityTrophyRenderer;renderTrophy(Lopenblocks/common/TrophyHandler$Trophy;DDDF)V"),
                   require = 1)
    private void onSet(TrophyHandler.Trophy renderer, double ratio, double renderWorld, double type, float x, Operation<Void> original) {
        rple$renderingInWorld.set(true);
        try {
            original.call(renderer, ratio, renderWorld, type, x);
        } finally {
            rple$renderingInWorld.set(false);
        }
    }

    @WrapWithCondition(method = "renderTrophy",
                       remap = false,
                       at = @At(value = "INVOKE", target = "Lopenmods/utils/render/RenderUtils;enableLightmap()V"),
                       require = 1)
    private static boolean onSet() {
        return rple$renderingInWorld.get();
    }
}
