/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.plugin;

import com.falsepattern.lib.mixin.IMixin;
import com.falsepattern.lib.mixin.ITargetedMod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Predicate;

import static com.falsepattern.lib.mixin.IMixin.PredicateHelpers.always;

@RequiredArgsConstructor
public enum Mixin implements IMixin {
    // @formatter:off
    BlockMixin(Side.COMMON, always(), "BlockMixin"),
    LuminaWorldMixin(Side.COMMON, always(), "lumina.WorldMixin"),
    LuminaChunkMixin(Side.COMMON, always(), "lumina.ChunkMixin"),
    LuminaEBSMixin(Side.COMMON, always(), "lumina.EBSMixin"),

    ChunkCacheMixin(Side.CLIENT, always(), "ChunkCacheMixin"),
    WorldMixin(Side.CLIENT, always(), "WorldMixin")
    ;
    // @formatter:on

    @Getter
    private final Side side;
    @Getter
    private final Predicate<List<ITargetedMod>> filter;
    @Getter
    private final String mixin;
}

