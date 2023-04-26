/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
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
    common_BlockMixin(Side.COMMON, always(), "BlockMixin"),
    common_lumina_WorldMixin(Side.COMMON, always(), "lumina.WorldMixin"),
    common_lumina_ChunkMixin(Side.COMMON, always(), "lumina.ChunkMixin"),
    common_lumina_EBSMixin(Side.COMMON, always(), "lumina.EBSMixin"),

    client_BlockFluidMixin(Side.CLIENT, always(), "BlockFluidMixin"),
    client_BlockLiquidMixin(Side.CLIENT, always(), "BlockLiquidMixin"),
    client_BlockMixin(Side.CLIENT, always(), "BlockMixin"),
    client_ChunkCacheMixin(Side.CLIENT, always(), "ChunkCacheMixin"),
    client_EntityRendererMixin(Side.CLIENT, always(), "EntityRendererMixin"),
    client_GuiInventoryMixin(Side.CLIENT, always(), "GuiInventoryMixin"),
    client_OpenGLHelperMixin(Side.CLIENT, always(), "OpenGLHelperMixin"),
    client_RenderBlocksMixin(Side.CLIENT, always(), "RenderBlocksMixin"),
    client_RendererLivingEntityMixin(Side.CLIENT, always(), "RendererLivingEntityMixin"),
    client_TessellatorMixin(Side.CLIENT, always(), "TessellatorMixin"),
    client_WorldMixin(Side.CLIENT, always(), "WorldMixin")
    ;
    // @formatter:on

    @Getter
    private final Side side;
    @Getter
    private final Predicate<List<ITargetedMod>> filter;
    @Getter
    private final String mixin;
}

