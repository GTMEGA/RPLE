/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.collection;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Getter
@EqualsAndHashCode
@Accessors(fluent = true, chain = false)
@RequiredArgsConstructor(access = PRIVATE)
public final class PriorityPair<T> implements Comparable<PriorityPair<T>> {
    private final T value;
    private final int priority;

    @Override
    public int compareTo(@NotNull PriorityPair<T> o) {
        return Integer.compare(priority, o.priority);
    }

    public static <T> PriorityPair<T> wrappedWithPriority(T value, int priority) {
        return new PriorityPair<>(value, priority);
    }
}
