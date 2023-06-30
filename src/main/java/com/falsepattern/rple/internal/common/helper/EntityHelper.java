/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.helper;

import com.falsepattern.rple.internal.Common;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.entity.Entity;

import java.util.*;

@UtilityClass
public final class EntityHelper {
    private static final Set<String> PERMITTED = new HashSet<>();
    private static final Map<Class<?>, Boolean> BLOCK_LIST_CACHE = new HashMap<>();
    private static final String[] GBFR_NAMES = {"func_70070_b", "getBrightnessForRender"};
    private static final Class<?>[] GBFR_ARGS = {float.class};

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

    public static void permit(String klass) {
        PERMITTED.add(klass);
    }

    public static boolean isOnBlockList(Class<?> klass) {
        if (BLOCK_LIST_CACHE.containsKey(klass)) {
            return BLOCK_LIST_CACHE.get(klass);
        }
        val classChain = new ArrayList<Class<?>>();
        boolean blocked = false;
        do {
            if (BLOCK_LIST_CACHE.containsKey(klass)) {
                blocked = BLOCK_LIST_CACHE.get(klass);
                break;
            }
            classChain.add(klass);
            if (!PERMITTED.contains(klass.getName()) && hasMethod(klass)) {
                blocked = true;
                break;
            }
        } while ((klass = klass.getSuperclass()) != null && klass != Entity.class);
        for (val c : classChain) {
            Common.LOG.debug("{} colors for {}", blocked ? "Blocking" : "Permitting", c.getName());
            BLOCK_LIST_CACHE.put(c, blocked);
        }
        return blocked;
    }
}
