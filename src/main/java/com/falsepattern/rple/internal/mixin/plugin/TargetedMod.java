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

@RequiredArgsConstructor
public enum TargetedMod implements ITargetedMod {
    OPTIFINE("OptiFine", false, startsWith("optifine")),
    CHISEL("Chisel", true, startsWith("chisel")),
    CARPENTERS_BLOCKS("Carpenter's Blocks", true, str -> str.matches("[cC]arpenter'?s( |-|_|%20|\\+)?[bB]locks.*")),
    ARCHITECTURE_CRAFT("ArchitectureCraft", true, startsWith("architecturecraft-")),
    PROJECT_RED_CORE("ProjectRed Core", true, startsWith("projectred").and(contains("base"))),
    PROJECT_RED_ILLUMINATION("ProjectRed Illumination", true, startsWith("projectred").and(contains("lighting"))),
    PROJECT_RED_COMBINED_JAR("ProjectRed Combined Jar", true, projectRedCombinedJarCondition()),
    APPLIED_ENERGISTICS_2("Applied Energistics 2", true, startsWith("appliedenergistics2-")),
    ENDER_IO("Ender IO", true, startsWith("enderio")),
    STORAGE_DRAWERS("Storage Drawers", true, startsWith("storagedrawers-")),
    CODE_CHICKEN_LIB("CodeChickenLib", true, startsWith("codechickenlib-")),
    FORGE_MULTI_PART("Forge Multi Part", true, startsWith("forgemultipart-")),
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
}
