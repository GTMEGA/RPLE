/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.common.util;

import lombok.val;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Function;

import static com.falsepattern.rple.internal.RightProperLightingEngine.createLogger;

public class ClassBlockList {
    private final Logger LOG;
    private final Set<String> PERMITTED = new HashSet<>();
    private final Map<Class<?>, Boolean> BLOCK_LIST_CACHE = new WeakHashMap<>();
    private final Function<Class<?>, Boolean> shouldBlockFn;
    private final String condName;
    private final Class<?> baseClass;

    public ClassBlockList(String name, String conditionTypeName, Class<?> baseClass, Function<Class<?>, Boolean> shouldBlockFn) {
        LOG = createLogger(name);
        this.shouldBlockFn = shouldBlockFn;
        this.condName = conditionTypeName;
        this.baseClass = baseClass;
    }

    public void permit(Class<?> klass) {
        permit(klass.getName());
    }

    public void permit(String klass) {
        PERMITTED.add(klass);
    }

    public boolean isOnBlockList(Class<?> klass) {
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
            if (!PERMITTED.contains(klass.getName()) && shouldBlockFn.apply(klass)) {
                blocked = true;
                break;
            }
        } while ((klass = klass.getSuperclass()) != null && klass != baseClass);
        for (val c : classChain) {
            LOG.debug("{} {} for {}", blocked ? "Blocking" : "Permitting", condName, c.getName());
            BLOCK_LIST_CACHE.put(c, blocked);
        }
        return blocked;
    }
}
