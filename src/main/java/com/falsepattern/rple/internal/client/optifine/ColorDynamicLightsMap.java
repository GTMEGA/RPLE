/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.client.optifine;

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
