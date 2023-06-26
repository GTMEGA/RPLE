/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.config.container;

import cpw.mods.fml.common.registry.GameRegistry;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Getter
@Accessors(fluent = true, chain = false)
@EqualsAndHashCode
public final class BlockReference implements Comparable<BlockReference> {
    public static final String INVALID_BLOCK_DOMAIN = "invalid_domain";
    public static final String INVALID_BLOCK_NAME = "invalid_name";

    private final String domain;
    private final String name;
    @Nullable
    private final Integer meta;

    private final boolean isValid;

    public BlockReference(Block block) {
        this(block, null);
    }

    public BlockReference(Block block, @Nullable Integer blockMeta) {
        var domain = "";
        var name = "";
        Integer meta = null;

        var isValid = false;

        try {
            val blockID = GameRegistry.findUniqueIdentifierFor(block);
            if (blockID == null)
                throw new IllegalArgumentException();

            domain = blockID.modId;
            name = blockID.name;
            meta = blockMeta;

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

    public @Nullable Block toBlock() {
        return GameRegistry.findBlock(domain, name);
    }

    public boolean isSameBlockType(BlockReference otherBlock) {
        if (otherBlock == null)
            return false;

        if (!this.domain.equals(otherBlock.domain))
            return false;
        if (!this.name.equals(otherBlock.name))
            return false;

        return true;
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
    public String toString() {
        if (meta != null)
            return domain + ":" + name + ":" + meta;
        return domain + ":" + name;
    }
}
