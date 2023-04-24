/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.LightMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import net.minecraft.client.gui.inventory.GuiInventory;

@Mixin(GuiInventory.class)
public abstract class GuiInventoryMixin {
    @Redirect(method = "func_147046_a",
              at = @At(value = "INVOKE",
                       target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
                       ordinal = 0),
              slice = @Slice(from = @At(value = "FIELD",
                                        target = "Lnet/minecraft/client/renderer/OpenGlHelper;lightmapTexUnit:I")),
              require = 1)
    private static void disableLightMap(int cap) {
        LightMap.disableAll();
    }
}
