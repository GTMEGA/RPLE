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

package com.falsepattern.rple.internal.mixin.mixins.client.enderio;

import com.falsepattern.rple.internal.mixin.helper.EnderIOHelper;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import lombok.val;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Pseudo
@Mixin(ConduitBundleRenderer.class)
public abstract class ConduitBundleRendererMixin_NH implements ISimpleBlockRenderingHandler {
    @Dynamic
    @ModifyConstant(method = "renderWorldBlock",
                    constant = @Constant(intValue = (15 << 20 | 15 << 4)),
                    remap = false,
                    require = 1)
    @SideOnly(Side.CLIENT)
    public float cacheTessellatorBrightness(float constant) {
        EnderIOHelper.cacheTessellatorBrightness((int) constant);
        return constant;
    }

    @Dynamic
    @Redirect(method = "renderWorldBlock",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/world/World;getLightBrightnessForSkyBlocks(IIII)I"),
              require = 1)
    @SideOnly(Side.CLIENT)
    public int cacheTessellatorBrightness(World world, int posX, int posY, int posZ, int minBrightnessCookie) {
        val tessellatorBrightness = world.getLightBrightnessForSkyBlocks(posX, posY, posZ, minBrightnessCookie);
        EnderIOHelper.cacheTessellatorBrightness(tessellatorBrightness);
        return tessellatorBrightness;
    }

    @Dynamic
    @Redirect(method = "renderConduits",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/renderer/Tessellator;setBrightness(I)V"),
              require = 1)
    @SideOnly(Side.CLIENT)
    public void cacheTessellatorBrightness(Tessellator instance, int brightnessBase) {
        instance.setBrightness(EnderIOHelper.loadTessellatorBrightness());
    }
}
