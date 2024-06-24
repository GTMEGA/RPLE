/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
