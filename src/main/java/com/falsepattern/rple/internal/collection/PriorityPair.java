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

package com.falsepattern.rple.internal.collection;

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
