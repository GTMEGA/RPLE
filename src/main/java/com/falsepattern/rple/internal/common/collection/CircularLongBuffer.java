/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
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
