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

import com.falsepattern.lib.mixin.ITargetedMod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

import static com.falsepattern.lib.mixin.ITargetedMod.PredicateHelpers.contains;
import static com.falsepattern.lib.mixin.ITargetedMod.PredicateHelpers.startsWith;
import static com.falsepattern.rple.internal.mixin.plugin.Extras.OPTIFINE_SHADERSMOD_VERSIONS;

@RequiredArgsConstructor
public enum TargetedMod implements ITargetedMod {
    OPTIFINE_WITHOUT_SHADERS("OptiFine without shaders", false,
                             contains("optifine").and(OPTIFINE_SHADERSMOD_VERSIONS.negate())),
    OPTIFINE_WITH_SHADERS("OptiFine with shaders", false, contains("optifine").and(OPTIFINE_SHADERSMOD_VERSIONS)),
    FASTCRAFT("FastCraft", false, contains("fastcraft")),
    CHISEL("Chisel", false, contains("chisel")),
    CARPENTERS_BLOCKS("Carpenter's Blocks", false, str -> str.matches("[cC]arpenter'?s( |-|_|%20|\\+)?[bB]locks.*")),
    ARCHITECTURE_CRAFT("ArchitectureCraft", false, contains("architecturecraft-")),
    PROJECT_RED_CORE("ProjectRed Core", false, contains("projectred").and(contains("base"))),
    PROJECT_RED_ILLUMINATION("ProjectRed Illumination", false, contains("projectred").and(contains("lighting"))),
    PROJECT_RED_MEGA_JAR("ProjectRed MEGA Jar", false, projectRedMegaJarCondition()),
    PROJECT_RED_OG_JAR("ProjectRed Combined Jar", false, projectRedCombinedJarCondition()),
    APPLIED_ENERGISTICS_2("Applied Energistics 2", false, contains("appliedenergistics2-")),
    ENDER_IO("Ender IO", false, contains("enderio")),
    STORAGE_DRAWERS("Storage Drawers", false, contains("storagedrawers-")),
    CODE_CHICKEN_LIB("CodeChickenLib", false, contains("codechickenlib-").or(contains("codechickencore-"))),
    NOT_ENOUGH_ITEMS("NotEnoughItems", false, contains("NotEnoughItems-")),
    FORGE_MULTI_PART("Forge Multi Part", false, contains("forgemultipart-")),
    COMPUTRONICS("Computronics", false, contains("Computronics-")),
    AM25("Ars Magica 2.5", false, contains("am2.5")),
    ;

    @Getter
    private final String modName;
    @Getter
    private final boolean loadInDevelopment;
    @Getter
    private final Predicate<String> condition;

    private static Predicate<String> projectRedCombinedJarCondition() {
        return ((Predicate<String>) str -> str.matches(".*projectred-\\d+\\.\\d+\\.\\d+.*"))
                .or(str -> str.matches(".*projectred-\\d+\\.\\d+\\.\\d+pre\\d+\\.\\d+.*"));
    }

    private static Predicate<String> projectRedMegaJarCondition() {
        return str -> str.contains("projectred-mc1.7.10") || str.contains("mega");
    }
}
