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

package com.falsepattern.rple.api.client;

import com.falsepattern.lib.StableAPI;
import com.falsepattern.rple.internal.client.render.EntityColorHandler;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

@StableAPI(since = "1.0.0")
public final class RPLEEntityAPI {
    private RPLEEntityAPI() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @StableAPI.Expose
    public static void permit(@NotNull Class<? extends Entity> entityClass) {
        EntityColorHandler.permit(entityClass);
    }

    /**
     * By default, any entity that overrides getBrightnessForRender will get a vanilla-style light value from Entity.getBrightnessForRender.
     * You can use this to remove this blocking logic from specific classes. Note: make sure all your superclasses
     * also behave correctly with colored lights!
     *
     * @param entityClassName The fully qualified class name
     */
    @StableAPI.Expose
    public static void permit(@NotNull String entityClassName) {
        EntityColorHandler.permit(entityClassName);
    }
}
