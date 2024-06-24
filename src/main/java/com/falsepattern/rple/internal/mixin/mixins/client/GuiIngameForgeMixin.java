/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.lumi.api.lighting.LightType;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.common.chunk.RPLEChunkContainer;
import com.falsepattern.rple.internal.common.chunk.RPLEChunkRoot;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.GuiIngameForge;

import java.util.ArrayList;

@Mixin(GuiIngameForge.class)
public abstract class GuiIngameForgeMixin extends GuiIngame {
    public GuiIngameForgeMixin(Minecraft p_i1036_1_) {
        super(p_i1036_1_);
    }

    @Redirect(method = "renderHUDText",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/multiplayer/WorldClient;blockExists(III)Z",
                       ordinal = 0),
              require = 1)
    private boolean hijackBlockInfo(WorldClient instance, int x, int y, int z) {
        return false;
    }

    @Redirect(method = "renderHUDText",
              at = @At(value = "INVOKE",
                       target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z",
                       ordinal = 1),
              slice = @Slice(from = @At(value = "INVOKE",
                                        target = "Lnet/minecraft/client/multiplayer/WorldClient;blockExists(III)Z",
                                        ordinal = 0)),
              require = 1)
    private boolean customData(ArrayList<String> left, Object e) {
        int x = MathHelper.floor_double(mc.thePlayer.posX);
        int y = MathHelper.floor_double(mc.thePlayer.posY);
        int z = MathHelper.floor_double(mc.thePlayer.posZ);
        if (mc.theWorld != null && mc.theWorld.blockExists(x, y, z)) {
            val chunk = mc.theWorld.getChunkFromBlockCoords(x, z);
            val cx = x & 15;
            val cz = z & 15;
            val r = ((RPLEChunkRoot)chunk).rple$chunk(ColorChannel.RED_CHANNEL);
            val g = ((RPLEChunkRoot)chunk).rple$chunk(ColorChannel.GREEN_CHANNEL);
            val b = ((RPLEChunkRoot)chunk).rple$chunk(ColorChannel.BLUE_CHANNEL);
            val br = r.lumi$getBrightness(LightType.BLOCK_LIGHT_TYPE, cx, y, cz);
            val bg = g.lumi$getBrightness(LightType.BLOCK_LIGHT_TYPE, cx, y, cz);
            val bb = b.lumi$getBrightness(LightType.BLOCK_LIGHT_TYPE, cx, y, cz);
            val sr = r.lumi$getBrightness(LightType.SKY_LIGHT_TYPE, cx, y, cz);
            val sg = g.lumi$getBrightness(LightType.SKY_LIGHT_TYPE, cx, y, cz);
            val sb = b.lumi$getBrightness(LightType.SKY_LIGHT_TYPE, cx, y, cz);
            left.add(String.format("lc: %d b: %s",
                                   chunk.getTopFilledSegment() + 15,
                                   chunk.getBiomeGenForWorldCoords(cx, cz, mc.theWorld.getWorldChunkManager()).biomeName));
            left.add(String.format("B r: %d g: %d b: %d", br, bg, bb));
            left.add(String.format("S r: %d g: %d b: %d", sr, sg, sb));
            return true;
        }
        return false;
    }
}
