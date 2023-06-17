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
import lombok.*;

import java.util.List;
import java.util.function.Predicate;

import static com.falsepattern.lib.mixin.IMixin.PredicateHelpers.*;
import static com.falsepattern.rple.internal.mixin.plugin.TargetedMod.*;

@RequiredArgsConstructor
public enum Mixin implements IMixin {
    // @formatter:off
    common_BlockMixin(Side.COMMON, always(), "BlockMixin"),
    common_lumina_WorldMixin(Side.COMMON, always(), "lumina.WorldMixin"),
    common_lumina_ChunkMixin(Side.COMMON, always(), "lumina.ChunkMixin"),
    common_lumina_EBSMixin(Side.COMMON, always(), "lumina.EBSMixin"),

    common_mrtjpcore_InstancedBlockMixin(Side.COMMON, require(MRTJPCORE), "mrtjpcore.InstancedBlockMixin"),
    common_projredillum_TileLampMixin(Side.COMMON, require(PROJECTRED_ILLUMINATION).or(require(PROJECTRED_COMBINEDJAR)), "projredillum.TileLampMixin"),

    client_BlockFluidMixin(Side.CLIENT, always(), "BlockFluidMixin"),
    client_BlockLiquidMixin(Side.CLIENT, always(), "BlockLiquidMixin"),
    client_BlockMixin(Side.CLIENT, always(), "BlockMixin"),
    client_ChunkCacheMixin(Side.CLIENT, always(), "ChunkCacheMixin"),
    client_EntityMixin(Side.CLIENT, always(), "EntityMixin"),
    client_EntityRendererMixin(Side.CLIENT, always(), "EntityRendererMixin"),
    client_OpenGLHelperMixin(Side.CLIENT, always(), "OpenGLHelperMixin"),
    client_RenderBlocksMixin(Side.CLIENT, always(), "RenderBlocksMixin"),
    client_TessellatorMixin(Side.CLIENT, always(), "TessellatorMixin"),
    client_Tessellator_NonOptiFineMixin(Side.CLIENT, avoid(OPTIFINE), "Tessellator_NonOptiFineMixin"),
    client_WorldMixin(Side.CLIENT, always(), "WorldMixin"),

    client_ShadersMixin(Side.CLIENT, require(OPTIFINE), "optifine.ShadersMixin"),
    client_ShaderTessMixin(Side.CLIENT, require(OPTIFINE), "optifine.ShaderTessMixin"),
    client_OptiFineTessellatorMixin(Side.CLIENT, require(OPTIFINE), "optifine.OptiFineTessellatorMixin"),

    client_RenderBlocksCTMMixin(Side.CLIENT, require(CHISEL), "chisel.RenderBlocksCTMMixin"),

    client_projredcore_RenderHaloMixin(Side.CLIENT, require(PROJECTRED_CORE).or(require(PROJECTRED_COMBINEDJAR)), "projredcore.RenderHaloMixin"),

    client_cclib_CCRenderStateMixin(Side.CLIENT, require(CCLIB), "cclib.CCRenderStateMixin"),
    client_cclib_LightMatrixMixin(Side.CLIENT, require(CCLIB), "cclib.LightMatrixMixin"),

    client_enderio_ConduitBundleRendererMixin(Side.CLIENT, require(ENDER_IO), "enderio.ConduitBundleRendererMixin"),
    client_enderio_DefaultConduitRendererMixin(Side.CLIENT, require(ENDER_IO), "enderio.DefaultConduitRendererMixin"),

    client_carpentersblocks_LightingHelperMixin(Side.CLIENT, require(CARPENTERS_BLOCKS), "carpentersblocks.LightingHelperMixin"),

    client_architecturecraft_Vector3Mixin(Side.CLIENT, require(ARCHITECTURECRAFT), "architecturecraft.Vector3Mixin"),
    client_architecturecraft_BaseWorldRenderTargetMixin(Side.CLIENT, require(ARCHITECTURECRAFT), "architecturecraft.BaseWorldRenderTargetMixin"),
    ;
    // @formatter:on

    @Getter
    private final Side side;
    @Getter
    private final Predicate<List<ITargetedMod>> filter;
    @Getter
    private final String mixin;
}

