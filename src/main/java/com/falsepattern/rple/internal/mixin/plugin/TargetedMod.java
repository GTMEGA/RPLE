/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.plugin;

import com.falsepattern.lib.mixin.ITargetedMod;
import lombok.*;

import java.util.function.Predicate;

import static com.falsepattern.lib.mixin.ITargetedMod.PredicateHelpers.contains;
import static com.falsepattern.lib.mixin.ITargetedMod.PredicateHelpers.startsWith;

@RequiredArgsConstructor
public enum TargetedMod implements ITargetedMod {
    OPTIFINE("OptiFine", false, startsWith("optifine")),
    CHISEL("Chisel", false, startsWith("chisel")),
    PROJECTRED_CORE("ProjectRed Core", false, startsWith("projectred").and(contains("base"))),
    PROJECTRED_ILLUMINATION("ProjectRed Illumination", false, startsWith("projectred").and(contains("lighting"))),
    MRTJPCORE("MRTJPCore", false, startsWith("mrtjpcore")),

    ;

    @Getter
    private final String modName;
    @Getter
    private final boolean loadInDevelopment;
    @Getter
    private final Predicate<String> condition;
}
