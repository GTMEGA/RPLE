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

package com.falsepattern.rple.internal.common.util;

import lombok.val;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Function;

import static com.falsepattern.rple.internal.common.util.LogHelper.createLogger;
import static com.falsepattern.rple.internal.common.config.RPLEConfig.Debug.DEBUG_CLASS_BLOCK_LIST;

public final class ClassBlockList {
    private final Logger log;
    private final Set<String> permitted = new HashSet<>();
    private final Map<Class<?>, Boolean> blockListCache = new WeakHashMap<>();
    private final Function<Class<?>, Boolean> shouldBlockFn;
    private final String condName;
    private final Class<?> baseClass;

    public ClassBlockList(String name, String conditionTypeName, Class<?> baseClass, Function<Class<?>, Boolean> shouldBlockFn) {
        this.log = createLogger(name);
        this.shouldBlockFn = shouldBlockFn;
        this.condName = conditionTypeName;
        this.baseClass = baseClass;
    }

    public void permit(Class<?> klass) {
        permit(klass.getName());
    }

    public void permit(String klass) {
        permitted.add(klass);
    }

    public boolean isOnBlockList(Class<?> klass) {
        if (blockListCache.containsKey(klass))
            return blockListCache.get(klass);

        val classChain = new ArrayList<Class<?>>();
        boolean blocked = false;
        do {
            if (blockListCache.containsKey(klass)) {
                blocked = blockListCache.get(klass);
                break;
            }
            classChain.add(klass);
            if (!permitted.contains(klass.getName()) && shouldBlockFn.apply(klass)) {
                blocked = true;
                break;
            }
        } while ((klass = klass.getSuperclass()) != null && klass != baseClass);
        for (val c : classChain) {
            if (LogHelper.shouldLogDebug(DEBUG_CLASS_BLOCK_LIST))
                log.debug("{} {} for {}", blocked ? "Blocking" : "Permitting", condName, c.getName());
            blockListCache.put(c, blocked);
        }
        return blocked;
    }
}
