/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
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
