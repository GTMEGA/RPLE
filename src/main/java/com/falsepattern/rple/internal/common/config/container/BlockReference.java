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

package com.falsepattern.rple.internal.common.config.container;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Accessors(fluent = true, chain = false)
@AllArgsConstructor(access = PRIVATE)
public final class BlockReference implements Comparable<BlockReference> {
    public static final String INVALID_BLOCK_DOMAIN = "invalid_domain";
    public static final String INVALID_BLOCK_NAME = "invalid_name";

    public static final BlockReference INVALID_BLOCK_REFERENCE =
            new BlockReference(INVALID_BLOCK_DOMAIN + ":" + INVALID_BLOCK_NAME);

    private final String domain;
    private final String name;
    @Nullable
    private final Integer meta;

    private final boolean isValid;

    public BlockReference(String blockID) {
        var domain = "";
        var name = "";
        Integer meta = null;

        var isValid = false;

        try {
            val subStrings = StringUtils.splitPreserveAllTokens(blockID, ':');
            val subStringCount = subStrings.length;
            if (subStringCount < 2 || subStringCount > 3)
                throw new IllegalArgumentException();

            domain = subStrings[0];
            name = subStrings[1];
            if (subStringCount > 2)
                meta = Integer.valueOf(subStrings[2]);

            if (domain == null || domain.isEmpty())
                throw new IllegalArgumentException();
            if (name == null || name.isEmpty())
                throw new IllegalArgumentException();
            if (meta != null && meta < 0)
                throw new IllegalArgumentException();

            isValid = true;
        } catch (IllegalArgumentException e) {
            domain = INVALID_BLOCK_DOMAIN;
            name = INVALID_BLOCK_NAME;
            meta = null;

            isValid = false;
        }

        this.domain = domain;
        this.name = name;
        this.meta = meta;

        this.isValid = isValid;
    }

    public Optional<Integer> meta() {
        return Optional.ofNullable(meta);
    }

    @Override
    public int compareTo(BlockReference otherBlock) {
        val domainComparison = this.domain.compareTo(otherBlock.domain);
        if (domainComparison != 0)
            return domainComparison;

        val nameComparison = this.name.compareTo(otherBlock.name);
        if (nameComparison != 0)
            return nameComparison;

        if (this.meta == null && otherBlock.meta == null)
            return 0;
        if (this.meta == null)
            return -1;
        if (otherBlock.meta == null)
            return 1;

        return this.meta.compareTo(otherBlock.meta);
    }

    @Override
    public int hashCode() {
        var hashCode = domain.hashCode();
        hashCode = (31 * hashCode) + name.hashCode();
        if (meta != null)
            hashCode = (31 * hashCode) + meta.hashCode();
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockReference))
            return false;

        val otherBlock = (BlockReference) obj;

        return this.domain.equals(otherBlock.domain) &&
               this.name.equals(otherBlock.name) &&
               this.meta().equals(otherBlock.meta());
    }

    @Override
    public String toString() {
        if (meta != null)
            return domain + ":" + name + ":" + meta;
        return domain + ":" + name;
    }
}
