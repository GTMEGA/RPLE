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

import com.gtnewhorizon.gtnhmixins.builders.ITargetMod;
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@RequiredArgsConstructor
public enum TargetMod implements ITargetMod {
    //coremods
    AppliedEnergistics2("appeng.core.AppEng"),
    ArsMagica2("am2.AMCore"),
    FairyLights("com.pau101.fairylights.FairyLights"),
    FastCraft("fastcraft.Tweaker"),
    OpenBlocks("openblocks.OpenBlocks"),
    SwanSong("com.ventooth.swansong.SwanSong"),
    //regular mods
    ArchitectureCraft("gcewing.architecture.ArchitectureCraft"),
    CarpentersBlocks("com.carpentersblocks.CarpentersBlocks"),
    Chisel("team.chisel.Chisel"),
    CodeChickenLib("codechicken.lib.math.MathHelper"),
    Computronics("pl.asie.computronics.Computronics"),
    DSurround("org.blockartistry.mod.DynSurround.Module"),
    EnderIO("crazypants.enderio.EnderIO"),
    MultiPart("codechicken.multipart.handler.MultipartMod"),
    NuclearTech("com.hbm.main.MainRegistry"),
    ProjectRedCore("mrtjp.projectred.ProjectRedCore"),
    ProjectRedCoreMEGA("mrtjp.projectred.ProjectRedCore", b -> b.testModAnnotation(null, null, v -> v.toLowerCase().contains("mega"))),
    ProjectRedIllumination("mrtjp.projectred.ProjectRedIllumination"),
    StorageDrawers("com.jaquadro.minecraft.storagedrawers.StorageDrawers"),
    ;
    TargetMod(@Language(value = "JAVA",
                        prefix = "import ",
                        suffix = ";") @NotNull String className) {
        this(className, null);
    }

    TargetMod(@Language(value = "JAVA",
                        prefix = "import ",
                        suffix = ";") @NotNull String className, @Nullable Consumer<TargetModBuilder> cfg) {
        builder = new TargetModBuilder();
        builder.setTargetClass(className);
        if (cfg != null) {
            cfg.accept(builder);
        }
    }

    @Getter
    private final TargetModBuilder builder;
}
