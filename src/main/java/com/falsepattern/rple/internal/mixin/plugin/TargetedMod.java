/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
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
                             startsWith("optifine").and(OPTIFINE_SHADERSMOD_VERSIONS.negate())),
    OPTIFINE_WITH_SHADERS("OptiFine with shaders", false, startsWith("optifine").and(OPTIFINE_SHADERSMOD_VERSIONS)),
    FASTCRAFT("FastCraft", false, startsWith("fastcraft")),
    CHISEL("Chisel", false, startsWith("chisel")),
    CARPENTERS_BLOCKS("Carpenter's Blocks", false, str -> str.matches("[cC]arpenter'?s( |-|_|%20|\\+)?[bB]locks.*")),
    ARCHITECTURE_CRAFT("ArchitectureCraft", false, startsWith("architecturecraft-")),
    PROJECT_RED_CORE("ProjectRed Core", false, startsWith("projectred").and(contains("base"))),
    PROJECT_RED_ILLUMINATION("ProjectRed Illumination", false, startsWith("projectred").and(contains("lighting"))),
    PROJECT_RED_COMBINED_JAR("ProjectRed Combined Jar", false, projectRedCombinedJarCondition().or(projectRedMegaJarCondition())),
    APPLIED_ENERGISTICS_2("Applied Energistics 2", false, startsWith("appliedenergistics2-")),
    ENDER_IO("Ender IO", false, startsWith("enderio")),
    STORAGE_DRAWERS("Storage Drawers", false, startsWith("storagedrawers-")),
    CODE_CHICKEN_LIB("CodeChickenLib", false, startsWith("codechickenlib-").or(startsWith("codechickencore-"))),
    NOT_ENOUGH_ITEMS("NotEnoughItems", false, startsWith("NotEnoughItems-")),
    FORGE_MULTI_PART("Forge Multi Part", false, startsWith("forgemultipart-")),
    ;

    @Getter
    private final String modName;
    @Getter
    private final boolean loadInDevelopment;
    @Getter
    private final Predicate<String> condition;

    private static Predicate<String> projectRedCombinedJarCondition() {
        return ((Predicate<String>) str -> str.matches("projectred-\\d+\\.\\d+\\.\\d+"))
                .or(str -> str.matches("projectred-\\d+\\.\\d+\\.\\d+pre\\d+\\.\\d+"));
    }

    private static Predicate<String> projectRedMegaJarCondition() {
        return str -> str.startsWith("projectred-mc1.7.10") || str.contains("mega");
    }
}
