/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.config.container;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Getter
@Accessors(fluent = true, chain = false)
public final class BlockReference {
    private final String domain;
    private final String name;
    @Nullable
    private final Integer meta;

    public BlockReference(String blockID) {
        var domain = "";
        var name = "";
        Integer meta = null;

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
        } catch (IllegalArgumentException e) {
            domain = "invalid_domain";
            name = "invalid_name";
            meta = null;
        }

        this.domain = domain;
        this.name = name;
        this.meta = meta;
    }

    public Optional<Integer> meta() {
        return Optional.ofNullable(meta);
    }

    @Override
    public String toString() {
        if (meta != null)
            return domain + ":" + name + ":" + meta;
        return domain + ":" + name;
    }
}
