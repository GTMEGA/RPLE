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

package com.falsepattern.rple.internal.client.dynlights;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorDynamicLightsMap {
    private final Map<Integer, ColorDynamicLight> map = new HashMap();
    private final List<ColorDynamicLight> list = new ArrayList();
    private boolean dirty = false;

    public ColorDynamicLightsMap() {
    }

    public ColorDynamicLight put(int id, ColorDynamicLight dynamicLight) {
        ColorDynamicLight old = this.map.put(id, dynamicLight);
        this.setDirty();
        return old;
    }

    public ColorDynamicLight get(int id) {
        return this.map.get(id);
    }

    public int size() {
        return this.map.size();
    }

    public ColorDynamicLight remove(int id) {
        ColorDynamicLight old = this.map.remove(id);
        if (old != null) {
            this.setDirty();
        }

        return old;
    }

    public void clear() {
        this.map.clear();
        this.setDirty();
    }

    private void setDirty() {
        this.dirty = true;
    }

    public List<ColorDynamicLight> valueList() {
        if (this.dirty) {
            this.list.clear();
            this.list.addAll(this.map.values());
            this.dirty = false;
        }

        return this.list;
    }
}
