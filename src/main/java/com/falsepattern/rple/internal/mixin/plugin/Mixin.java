/*
 * Copyright (c) 2023 FalsePattern, Ven
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

import static com.falsepattern.lib.mixin.IMixin.PredicateHelpers.*;
import static com.falsepattern.lib.mixin.IMixin.Side.CLIENT;
import static com.falsepattern.lib.mixin.IMixin.Side.COMMON;
import static com.falsepattern.rple.internal.mixin.plugin.TargetedMod.*;

@RequiredArgsConstructor
public enum Mixin implements IMixin {
    // region Colorization Hooks
    common_BlockMixin(COMMON, always(), "BlockMixin"),

    client_BlockFluidMixin(CLIENT, always(), "BlockFluidMixin"),
    client_BlockLiquidMixin(CLIENT, always(), "BlockLiquidMixin"),
    client_ChunkCacheMixin(CLIENT, always(), "ChunkCacheMixin"),
    client_EntityMixin(CLIENT, always(), "EntityMixin"),
    client_EntityRendererMixin(CLIENT, always(), "EntityRendererMixin"),
    client_OpenGLHelperMixin(CLIENT, always(), "OpenGLHelperMixin"),
    client_RenderBlocksMixin(CLIENT, always(), "RenderBlocksMixin"),
    client_TessellatorMixin(CLIENT, always(), "TessellatorMixin"),
    client_WorldMixin(CLIENT, always(), "WorldMixin"),

    client_Tessellator_NonOptiFineMixin(CLIENT, avoid(OPTIFINE), "Tessellator_NonOptiFineMixin"),
    // endregion

    // region Right Proper Lighting Engine Implementation
    common_rple_RPLEBlockInitImplMixin(COMMON, always(), "rple.RPLEBlockInitImplMixin"),
    common_rple_RPLEBlockImplMixin(COMMON, always(), "rple.RPLEBlockImplMixin"),
    common_rple_RPLEBlockRootImplMixin(COMMON, always(), "rple.RPLEBlockRootImplMixin"),

    common_rple_RPLEWorldRootImplMixin(COMMON, always(), "rple.RPLEWorldRootImplMixin"),
    common_rple_RPLEChunkRootImplMixin(COMMON, always(), "rple.RPLEChunkRootImplMixin"),
    common_rple_RPLESubChunkRootImplMixin(COMMON, always(), "rple.RPLESubChunkRootImplMixin"),
    // endregion

    // region OptiFine Compatibility
    client_optifine_ShadersMixin(CLIENT, require(OPTIFINE), "optifine.ShadersMixin"),
    client_optifine_ShaderTessMixin(CLIENT, require(OPTIFINE), "optifine.ShaderTessMixin"),
    client_optifine_OptiFineTessellatorMixin(CLIENT, require(OPTIFINE), "optifine.OptiFineTessellatorMixin"),
    // endregion

    // region Chisel Compatibility
    client_chisel_RenderBlocksCTMMixin(CLIENT, require(CHISEL), "chisel.RenderBlocksCTMMixin"),
    client_chisel_RenderBlocksEldritchMixin(CLIENT, require(CHISEL), "chisel.RenderBlocksEldritchMixin"),
    // endregion

    // region Carpenter's Blocks Compatibility
    client_carpentersblocks_LightingHelperMixin(CLIENT, require(CARPENTERS_BLOCKS), "carpentersblocks.LightingHelperMixin"),
    // endregion

    // region ArchitectureCraft Compatibility
    client_architecturecraft_Vector3Mixin(CLIENT, require(ARCHITECTURE_CRAFT), "architecturecraft.Vector3Mixin"),
    client_architecturecraft_BaseWorldRenderTargetMixin(CLIENT, require(ARCHITECTURE_CRAFT), "architecturecraft.BaseWorldRenderTargetMixin"),
    // endregion

    // region Project Red Compatibility
    common_projectred_ILightMixin(COMMON, projectRedLampsFilter(), "projectred.ILightMixin"),

    client_projectred_RenderHaloMixin(CLIENT, projectRedLampsFilter(), "projectred.RenderHaloMixin"),
    // endregion

    // region Applied Energistics 2 Compatibility
    common_appliedenergistics2_BlockCableBusMixin(COMMON, require(APPLIED_ENERGISTICS_2), "appliedenergistics2.BlockCableBusMixin"),
    common_appliedenergistics2_CableBusContainerMixin(COMMON, require(APPLIED_ENERGISTICS_2), "appliedenergistics2.CableBusContainerMixin"),

    client_appliedenergistics2_RenderBlocksWorkaroundMixin(CLIENT, require(APPLIED_ENERGISTICS_2), "appliedenergistics2.RenderBlocksWorkaroundMixin"),
    // endregion

    // region Ender IO Compatibility
    client_enderio_ConduitBundleRendererMixin(CLIENT, require(ENDER_IO), "enderio.ConduitBundleRendererMixin"),
    client_enderio_DefaultConduitRendererMixin(CLIENT, require(ENDER_IO), "enderio.DefaultConduitRendererMixin"),
    // endregion

    // region Storage Drawers Compatibility
    client_storagedrawers_RenderUtilMixin(CLIENT, require(STORAGE_DRAWERS), "storagedrawers.RenderUtilMixin"),
    client_storagedrawers_RenderHelperAOMixin(CLIENT, require(STORAGE_DRAWERS), "storagedrawers.RenderHelperAOMixin"),
    // endregion

    // region CodeChickenLib Compatibility
    client_codechickenlib_CCRenderStateMixin(CLIENT, require(CODE_CHICKEN_LIB), "codechickenlib.CCRenderStateMixin"),
    client_codechickenlib_LightMatrixMixin(CLIENT, require(CODE_CHICKEN_LIB), "codechickenlib.LightMatrixMixin"),
    // endregion
    ;

    @Getter
    private final Side side;
    @Getter
    private final Predicate<List<ITargetedMod>> filter;
    @Getter
    private final String mixin;

    private static Predicate<List<ITargetedMod>> projectRedLampsFilter() {
        return require(PROJECT_RED_COMBINED_JAR).or(require(PROJECT_RED_CORE).and(require(PROJECT_RED_ILLUMINATION)));
    }
}

