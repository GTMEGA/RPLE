/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
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
                       target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z",
                       ordinal = 0),
              slice = @Slice(from = @At(value = "INVOKE",
                                        target = "Lnet/minecraft/client/multiplayer/WorldClient;blockExists(III)Z",
                                        ordinal = 0)),
              require = 1)
    private boolean customData(ArrayList<String> left, Object e) {
        val original = (String) e;
        val startOffset = original.indexOf(" bl: ");
        val prefix = original.substring(0, startOffset);
        left.add(prefix);
        int x = MathHelper.floor_double(mc.thePlayer.posX);
        int y = MathHelper.floor_double(mc.thePlayer.posY);
        int z = MathHelper.floor_double(mc.thePlayer.posZ);
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
        left.add(String.format("B r: %d g: %d b: %d", br, bg, bb));
        left.add(String.format("S r: %d g: %d b: %d", sr, sg, sb));
        return true;
    }
}
