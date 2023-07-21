/*
 * Copyright (c) 2023 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.falsepattern.rple.internal.common.collection;

import lombok.val;

import java.util.concurrent.atomic.AtomicInteger;


public final class CircularLongBuffer {
    private final int size;
    private final long[] buffer;
    private final AtomicInteger counter;

    public CircularLongBuffer(int size) {
        this.size = size;
        this.buffer = new long[size];
        this.counter = new AtomicInteger(0);
    }

    /**
     * Inserts the specified element at the current head and updates the head.
     *
     * @param value the element to add
     * @return the index of the element in the buffer
     */
    public int put(long value) {
        val index = counter.getAndUpdate(i -> (i + 1) % size);
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
