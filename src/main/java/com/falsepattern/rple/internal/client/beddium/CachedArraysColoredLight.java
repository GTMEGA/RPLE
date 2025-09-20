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

package com.falsepattern.rple.internal.client.beddium;

import com.falsepattern.rple.api.client.CookieMonster;
import com.ventooth.beddium.api.cache.CachedArrays;
import lombok.val;

import java.util.Arrays;

public class CachedArraysColoredLight extends CachedArrays {
    private final long[] combinedLights;

    public CachedArraysColoredLight(int capacity) {
        super(capacity);
        combinedLights = new long[capacity];
        Arrays.fill(combinedLights, -1);
    }

    public void reset() {
        super.reset();
        Arrays.fill(combinedLights, -1);
    }

    @Override
    public int getLight(int index) {
        val l = combinedLights[index];
        if (l == -1) {
            return -1;
        }
        return CookieMonster.cookieFromRGB64(l);
    }

    @Override
    public void putLight(int index, int value) {
        combinedLights[index] = CookieMonster.RGB64FromCookie(value);
    }
}
