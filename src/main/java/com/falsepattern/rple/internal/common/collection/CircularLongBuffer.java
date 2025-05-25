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

package com.falsepattern.rple.internal.common.collection;

import lombok.val;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;


public final class CircularLongBuffer {
    private final int size;
    private final long[] buffer;
    private int counter;

    public CircularLongBuffer(int size) {
        this.size = size;
        this.buffer = new long[size];
        this.counter = 0;
    }


    private int getAndIncrementIndexModular() {
        int i = counter;
        counter = (counter + 1) % size;
        return i;
    }

    /**
     * Inserts the specified element at the current head and updates the head.
     *
     * @param value the element to add
     * @return the index of the element in the buffer
     */
    public int put(long value) {
        val index = getAndIncrementIndexModular();
        buffer[index] = value;
        return index;
    }

    /**
     * Retrieves the element at the specified index.
     *
     * @param index the index of the element to return
     * @return the element at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public long get(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        return buffer[index];
    }

    /**
     * Returns the current size of the buffer.
     *
     * @return the size of the buffer
     */
    public int size() {
        return size;
    }
}
