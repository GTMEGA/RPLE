/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.mixin.hook.ColoredLightingHooks;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {
    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Redirect(method = "getBrightnessForRender",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/world/World;getLightBrightnessForSkyBlocks(IIII)I"),
              require = 1)
    private int getBrightnessForRender(World world, int posX, int posY, int posZ, int cookieMinBlockLight) {
        return ColoredLightingHooks.getEntityRGBBrightnessForTessellator(thiz(),
                                                                         world,
                                                                         posX,
                                                                         posY,
                                                                         posZ,
                                                                         cookieMinBlockLight);
    }

    private Entity thiz() {
        return (Entity) (Object) this;
    }
}
