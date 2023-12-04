/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.mixins.client.optifine;

import com.falsepattern.rple.internal.client.optifine.ColorDynamicLights;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;

@Pseudo
@Mixin(targets = "DynamicLights",
       remap = false)
public abstract class DynamicLightsMixin {
    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static void entityAdded(Entity entityIn, RenderGlobal renderGlobal) {
        ColorDynamicLights.entityAdded(entityIn, renderGlobal);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static void entityRemoved(Entity entityIn, RenderGlobal renderGlobal) {
        ColorDynamicLights.entityRemoved(entityIn, renderGlobal);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static void update(RenderGlobal renderGlobal) {
        ColorDynamicLights.update(renderGlobal);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static int getCombinedLight(int x, int y, int z, int combinedLight) {
        return ColorDynamicLights.getCombinedLight(x, y, z, combinedLight);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static int getCombinedLight(Entity entity, int combinedLight) {
        return ColorDynamicLights.getCombinedLight(entity, combinedLight);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static void removeLights(RenderGlobal renderGlobal) {
        ColorDynamicLights.removeLights(renderGlobal);
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static void clear() {
        ColorDynamicLights.clear();
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static int getCount() {
        return ColorDynamicLights.getCount();
    }
}
