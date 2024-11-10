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

package com.falsepattern.rple.internal.mixin.plugin;

import com.falsepattern.lib.mixin.IMixin;
import com.falsepattern.lib.mixin.ITargetedMod;
import com.falsepattern.rple.internal.common.config.RPLEConfig;
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
    common_ChunkMixin(COMMON, always(), "ChunkMixin"),
    common_ExtendedBlockStorageMixin(COMMON, always(), "ExtendedBlockStorageMixin"),

    client_BlockFluidMixin(CLIENT, always(), "BlockFluidMixin"),
    client_BlockLiquidMixin(CLIENT, always(), "BlockLiquidMixin"),
    client_ChunkCacheMixin(CLIENT, always(), "ChunkCacheMixin"),
    client_EntityMixin(CLIENT, always(), "EntityMixin"),
    client_EntityRendererMixin(CLIENT, always(), "EntityRendererMixin"),
    client_GuiIngameForgeMixin(CLIENT, always(), "GuiIngameForgeMixin"),
    client_OpenGLHelperMixin(CLIENT, always(), "OpenGLHelperMixin"),
    client_RenderBlocksMixin(CLIENT, always(), "RenderBlocksMixin"),
    client_TessellatorMixin(CLIENT, always(), "TessellatorMixin"),
    client_WorldMixin(CLIENT, always(), "WorldMixin"),

    client_RendererLivingEntityMixin(CLIENT, always(), "RendererLivingEntityMixin"),
    client_RenderMixin(CLIENT, always(), "RenderMixin"),

    client_Tessellator_NonOptiFineMixin(CLIENT, avoid(OPTIFINE_WITH_SHADERS).and(avoid(OPTIFINE_WITHOUT_SHADERS)), "Tessellator_NonOptiFineMixin"),
    // endregion

    // region Right Proper Lighting Engine Implementation
    common_rple_RPLEBlockInitImplMixin(COMMON, always(), "rple.RPLEBlockInitImplMixin"),
    common_rple_RPLEBlockImplMixin(COMMON, always(), "rple.RPLEBlockImplMixin"),
    common_rple_RPLEBlockRootImplMixin(COMMON, always(), "rple.RPLEBlockRootImplMixin"),

    common_rple_RPLEWorldRootImplMixin(COMMON, always(), "rple.RPLEWorldRootImplMixin"),
    common_rple_RPLEChunkRootImplMixin(COMMON, always(), "rple.RPLEChunkRootImplMixin"),
    common_rple_RPLESubChunkRootImplMixin(COMMON, always(), "rple.RPLESubChunkRootImplMixin"),
    common_rple_RPLEBlockCacheRootImplMixin(COMMON, always(), "rple.RPLEBlockCacheRootImplMixin"),

    client_rple_RPLEClientBlockStorageImplMixin(CLIENT, always(), "rple.RPLEClientBlockStorageImplMixin"),
    client_rple_RPLEClientChunkImplMixin(CLIENT, always(), "rple.RPLEClientChunkImplMixin"),
    client_rple_RPLEClientSubChunkImplMixin(CLIENT, always(), "rple.RPLEClientSubChunkImplMixin"),
    // endregion

    // region OptiFine Compatibility
    client_optifine_OpenGLHelperMixin(CLIENT, require(OPTIFINE_WITH_SHADERS), "optifine.OpenGLHelperMixin"),
    client_optifine_ShadersMixin(CLIENT, require(OPTIFINE_WITH_SHADERS), "optifine.ShadersMixin"),
    client_optifine_ShaderTessMixin(CLIENT, require(OPTIFINE_WITH_SHADERS), "optifine.ShaderTessMixin"),
    client_optifine_OptiFineTessellatorMixin(CLIENT, require(OPTIFINE_WITH_SHADERS).and(avoid(OPTIFINE_WITHOUT_SHADERS)), "optifine.OptiFineTessellatorMixin"),
    client_optifine_OptiFineTessellator_NonShaderMixin(CLIENT, require(OPTIFINE_WITHOUT_SHADERS).and(avoid(OPTIFINE_WITH_SHADERS)), "optifine.OptiFineTessellator_NonShaderMixin"),
    // endregion

    //region FastCraft Compatibility
    client_fastcraft_ClippingHelperImplMixin(CLIENT, require(FASTCRAFT), "fastcraft.ClippingHelperImplMixin"),
    client_fastcraft_FrustumMixin(CLIENT, require(FASTCRAFT), "fastcraft.FrustumMixin"),
    //endregion FastCraft Compatibility

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
    common_projectred_ILightMixin(COMMON, projectRedILightFilter().and(condition(() -> !RPLEConfig.Compat.WEAKER_PROJECTRED_MIXINS)), "projectred.ILightMixin"),
    common_projectred_weaker_BaseLightPartMixin(COMMON, projectRedILightFilter().and(condition(() -> RPLEConfig.Compat.WEAKER_PROJECTRED_MIXINS)), "projectred.weaker.BaseLightPartMixin"),
    common_projectred_weaker_LightButtonPartMixin(COMMON, projectRedILightFilter().and(condition(() -> RPLEConfig.Compat.WEAKER_PROJECTRED_MIXINS)), "projectred.weaker.LightButtonPartMixin"),
    common_projectred_weaker_TileLampMixin(COMMON, projectRedILightFilter().and(condition(() -> RPLEConfig.Compat.WEAKER_PROJECTRED_MIXINS)), "projectred.weaker.TileLampMixin"),

    client_projectred_RenderHaloMixin(CLIENT, projectRedLampsFilter(), "projectred.RenderHaloMixin"),
    client_projectred_LampTESRMixin(CLIENT, projectRedLampsFilter(), "projectred.LampTESRMixin"),
    client_projectred_LightObjectMixin(CLIENT, projectRedLampsFilter(), "projectred.LightObjectMixin"),
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

    // region Not Enough Items Compatibility
    client_notenoughitems_WorldOverlayRenderer(CLIENT, require(NOT_ENOUGH_ITEMS), "notenoughitems.WorldOverlayRendererMixin"),
    // endregion

    // region Forge Multi Part Compatibility
    client_multipart_TileMultipartMixin(COMMON, require(FORGE_MULTI_PART), "multipart.TileMultipartMixin"),
    // endregion

    // region Computronics Compatibility
    client_computronics_TileColorfulLampMixin(COMMON, require(COMPUTRONICS), "computronics.TileColorfulLampMixin"),
    // endregion

    // TODO proper category
    RenderManagerMixin(CLIENT, always(), "RenderManagerMixin")
    ;

    @Getter
    private final Side side;
    @Getter
    private final Predicate<List<ITargetedMod>> filter;
    @Getter
    private final String mixin;

    private static Predicate<List<ITargetedMod>> projectRedLampsFilter() {
        return require(PROJECT_RED_OG_JAR).or(require(PROJECT_RED_MEGA_JAR)).or(require(PROJECT_RED_CORE).and(require(PROJECT_RED_ILLUMINATION)));
    }
    private static Predicate<List<ITargetedMod>> projectRedILightFilter() {
        return require(PROJECT_RED_OG_JAR).or(require(PROJECT_RED_CORE).and(require(PROJECT_RED_ILLUMINATION)));
    }
}

