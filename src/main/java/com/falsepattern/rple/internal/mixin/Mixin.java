/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
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

package com.falsepattern.rple.internal.mixin;

import com.falsepattern.lib.mixin.v2.MixinHelper;
import com.falsepattern.lib.mixin.v2.SidedMixins;
import com.falsepattern.lib.mixin.v2.TaggedMod;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.common.config.RPLEConfig;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;

import java.util.function.BooleanSupplier;

import static com.falsepattern.lib.mixin.v2.MixinHelper.avoid;
import static com.falsepattern.lib.mixin.v2.MixinHelper.builder;
import static com.falsepattern.lib.mixin.v2.MixinHelper.mods;
import static com.falsepattern.lib.mixin.v2.MixinHelper.require;
import static com.falsepattern.rple.internal.mixin.TargetMod.AppliedEnergistics2;
import static com.falsepattern.rple.internal.mixin.TargetMod.ArchitectureCraft;
import static com.falsepattern.rple.internal.mixin.TargetMod.ArsMagica2;
import static com.falsepattern.rple.internal.mixin.TargetMod.CarpentersBlocks;
import static com.falsepattern.rple.internal.mixin.TargetMod.Chisel;
import static com.falsepattern.rple.internal.mixin.TargetMod.CodeChickenLib;
import static com.falsepattern.rple.internal.mixin.TargetMod.Computronics;
import static com.falsepattern.rple.internal.mixin.TargetMod.DSurround;
import static com.falsepattern.rple.internal.mixin.TargetMod.EnderIO;
import static com.falsepattern.rple.internal.mixin.TargetMod.FairyLights;
import static com.falsepattern.rple.internal.mixin.TargetMod.FastCraft;
import static com.falsepattern.rple.internal.mixin.TargetMod.MultiPart;
import static com.falsepattern.rple.internal.mixin.TargetMod.NuclearTech;
import static com.falsepattern.rple.internal.mixin.TargetMod.OpenBlocks;
import static com.falsepattern.rple.internal.mixin.TargetMod.ProjectRedCore;
import static com.falsepattern.rple.internal.mixin.TargetMod.ProjectRedCoreMEGA;
import static com.falsepattern.rple.internal.mixin.TargetMod.ProjectRedIllumination;
import static com.falsepattern.rple.internal.mixin.TargetMod.StorageDrawers;
import static com.falsepattern.rple.internal.mixin.TargetMod.SwanSong;

@SuppressWarnings("UnstableApiUsage")
@RequiredArgsConstructor
public enum Mixin implements IMixins {
    //@formatter:off

    // region Base
    Base_ColorizationHooks(Phase.EARLY,
                           common("BlockMixin",
                                  "ChunkMixin",
                                  "ExtendedBlockStorageMixin"),
                           client("BlockFluidMixin",
                                  "BlockFluidMixin",
                                  "BlockLiquidMixin",
                                  "ChunkCacheMixin",
                                  "EntityMixin",
                                  "EntityRendererMixin",
                                  "GuiIngameForgeMixin",
                                  "OpenGLHelperMixin",
                                  "RenderBlocksMixin",
                                  "TessellatorMixin",
                                  "WorldMixin",
                                  "falsetweaks.BrightnessMathMixin")),

    Base_RpleImplementation(Phase.EARLY,
                            common("rple.RPLEBlockInitImplMixin",
                                   "rple.RPLEBlockImplMixin",
                                   "rple.RPLEBlockRootImplMixin",
                                   "rple.RPLEWorldRootImplMixin",
                                   "rple.RPLEChunkRootImplMixin",
                                   "rple.RPLESubChunkRootImplMixin",
                                   "rple.RPLEBlockCacheRootImplMixin"),
                            client("rple.RPLEClientBlockStorageImplMixin",
                                   "rple.RPLEClientChunkImplMixin",
                                   "rple.RPLEClientSubChunkImplMixin")),

    Base_HardcoreDarkness(Phase.EARLY,
                          client("hd.GuiOptionSliderMixin",
                                 "hd.WorldMixin",
                                 "hd.WorldProviderMixin",
                                 "hd.WorldProviderHellMixin")),

    Base_OpenGLLeakFix(Phase.EARLY,
                       client("RenderManagerMixin")),

    // endregion Base

    // region Compat

    Compat_AppliedEnergistics2(Phase.EARLY,
                               mods(require(AppliedEnergistics2)),
                               common("appliedenergistics2.BlockCableBusMixin",
                                      "appliedenergistics2.CableBusContainerMixin"),
                               client("appliedenergistics2.RenderBlocksWorkaroundMixin")),

    Compat_ArchitectureCraft(Phase.LATE,
                             require(ArchitectureCraft),
                             client("architecturecraft.Vector3Mixin",
                                    "architecturecraft.BaseWorldRenderTargetMixin")),

    Compat_ArsMagica2(Phase.EARLY,
                     require(ArsMagica2),
                      client("am25.AMParticleMixin")),

    Compat_CarpentersBlocks(Phase.LATE,
                            require(CarpentersBlocks),
                            client("carpentersblocks.LightingHelperMixin")),

    Compat_Chisel(Phase.LATE,
                  require(Chisel),
                  client("chisel.RenderBlocksCTMMixin",
                         "chisel.RenderBlocksEldritchMixin")),

    Compat_CodeChickenLib(Phase.LATE,
                          require(CodeChickenLib),
                          client("codechickenlib.CCRenderStateMixin",
                                 "codechickenlib.LightMatrixMixin")),

    Compat_Computronics(Phase.LATE,
                        require(Computronics),
                        common("computronics.TileColorfulLampMixin")),

    Compat_DynSurround(Phase.LATE,
                       require(DSurround),
                       client("dsurround.StormRendererMixin")),

    Compat_EnderIO(Phase.LATE,
                   require(EnderIO),
                   client("enderio.ConduitBundleRendererMixin",
                          "enderio.DefaultConduitRendererMixin")),

    Compat_FairyLights(Phase.EARLY,
                       require(FairyLights),
                       client("fairylights.ConnectionRendererMixin",
                              "fairylights.ModelConnectionMixin")),

    Compat_FastCraft(Phase.EARLY,
                     require(FastCraft),
                     client("fastcraft.ClippingHelperImplMixin",
                            "fastcraft.FrustumMixin")),

    Compat_MultiPart(Phase.LATE,
                     require(MultiPart),
                     common("multipart.TileMultipartMixin")),

    Compat_NuclearTech(Phase.LATE,
                       require(NuclearTech),
                       client("hbm.VertInfoMixin")),

    Compat_OpenBlocks(Phase.EARLY,
                      require(OpenBlocks),
                      client("openblocks.TileEntityTrophyRendererMixin")),

    Compat_ProjectRed_Light_Strong(Phase.LATE,
                                   () -> !RPLEConfig.Compat.WEAKER_PROJECTRED_MIXINS,
                                   mods(require(ProjectRedIllumination), avoid(ProjectRedCoreMEGA)),
                                   common("projectred.ILightMixin")),
    Compat_ProjectRed_Light_Weak(Phase.LATE,
                                 () -> RPLEConfig.Compat.WEAKER_PROJECTRED_MIXINS,
                                 mods(require(ProjectRedIllumination), avoid(ProjectRedCoreMEGA)),
                                 common("projectred.weaker.BaseLightPartMixin",
                                        "projectred.weaker.LightButtonPartMixin",
                                        "projectred.weaker.TileLampMixin")),
    Compat_ProjectRed_Lamps(Phase.LATE,
                            mods(require(ProjectRedCore), require(ProjectRedIllumination)),
                            client("projectred.LampTESRMixin",
                                   "projectred.LightObjectMixin",
                                   "projectred.RenderHaloMixin")),

    Compat_StorageDrawers(Phase.LATE,
                          require(StorageDrawers),
                          client("storagedrawers.RenderHelperAOMixin",
                                 "storagedrawers.RenderUtilMixin")),

    Compat_SwanSong(Phase.EARLY,
                    require(SwanSong),
                    client("swansong.ShaderLoaderMixin",
                           "swansong.ShaderSamplers$BuilderMixin",
                           "swansong.ShaderStateMixin",
                           "swansong.ShaderTessMixin",
                           "swansong.ShaderVertMixin")),

    // endregion Compat

    //@formatter:on
    ;

    public static final int RPLE_INIT_MIXIN_PRIORITY = 980;
    public static final int RPLE_ROOT_MIXIN_PRIORITY = 990;
    public static final int POST_LUMI_MIXIN_PRIORITY = 1010;

    //region boilerplate
    @Getter
    private final MixinBuilder builder;

    Mixin(Phase phase, SidedMixins... mixins) {
        this(builder(mixins).setPhase(phase));
    }

    Mixin(Phase phase, BooleanSupplier cond, SidedMixins... mixins) {
        this(builder(cond, mixins).setPhase(phase));
    }

    Mixin(Phase phase, TaggedMod mod, SidedMixins... mixins) {
        this(builder(mod, mixins).setPhase(phase));
    }

    Mixin(Phase phase, TaggedMod[] mods, SidedMixins... mixins) {
        this(builder(mods, mixins).setPhase(phase));
    }

    Mixin(Phase phase, BooleanSupplier cond, TaggedMod mod, SidedMixins... mixins) {
        this(builder(cond, mod, mixins).setPhase(phase));
    }

    Mixin(Phase phase, BooleanSupplier cond, TaggedMod[] mods, SidedMixins... mixins) {
        this(builder(cond, mods, mixins).setPhase(phase));
    }

    private static SidedMixins common(@Language(value = "JAVA",
                                                prefix = "import " + Tags.GROUP_NAME + ".internal.mixin.mixins.common.",
                                                suffix = ";") String... mixins) {
        return MixinHelper.common(mixins);
    }

    private static SidedMixins client(@Language(value = "JAVA",
                                                prefix = "import " + Tags.GROUP_NAME + ".internal.mixin.mixins.client.",
                                                suffix = ";") String... mixins) {
        return MixinHelper.client(mixins);
    }

    private static SidedMixins server(@Language(value = "JAVA",
                                                prefix = "import " + Tags.GROUP_NAME + ".internal.mixin.mixins.server.",
                                                suffix = ";") String... mixins) {
        return MixinHelper.server(mixins);
    }
    //endregion
}
