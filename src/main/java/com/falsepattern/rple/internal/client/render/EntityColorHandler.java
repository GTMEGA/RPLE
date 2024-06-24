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

package com.falsepattern.rple.internal.client.render;

import com.falsepattern.rple.internal.common.util.ClassBlockList;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.entity.Entity;

@UtilityClass
public final class EntityColorHandler {
    private static final ClassBlockList BLOCK_LIST = new ClassBlockList("EntityColorHandler",
                                                                        "Color",
                                                                        Entity.class,
                                                                        EntityColorHandler::hasMethod);

    private static final String[] GBFR_NAMES = {"func_70070_b", "getBrightnessForRender"};
    private static final Class<?>[] GBFR_ARGS = {float.class};

    public static void permit(Class<? extends Entity> klass) {
        BLOCK_LIST.permit(klass);
    }

    public static void permit(String klass) {
        BLOCK_LIST.permit(klass);
    }

    public static boolean isOnBlockList(Class<?> klass) {
        return BLOCK_LIST.isOnBlockList(klass);
    }

    private static boolean hasMethod(Class<?> klass) {
        boolean found = false;
        for (val name : GBFR_NAMES) {
            try {
                klass.getDeclaredMethod(name, GBFR_ARGS);
                found = true;
                break;
            } catch (Exception ignored) {
            }
        }
        return found;
    }
}
