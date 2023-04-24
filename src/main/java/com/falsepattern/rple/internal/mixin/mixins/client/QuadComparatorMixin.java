/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.util.QuadComparator;

@Mixin(QuadComparator.class)
public abstract class QuadComparatorMixin {
    @ModifyConstant(method = "compare(Ljava/lang/Integer;Ljava/lang/Integer;)I",
                    constant = {@Constant(intValue = 8),
                                @Constant(intValue = 9),
                                @Constant(intValue = 10),
                                @Constant(intValue = 16),
                                @Constant(intValue = 17),
                                @Constant(intValue = 18),
                                @Constant(intValue = 24),
                                @Constant(intValue = 25),
                                @Constant(intValue = 26)},
                    require = 18)
    private int extendOffsets(int constant) {
        return Constants.extendBytesPerVertex(constant) + constant % Constants.BYTES_PER_VERTEX_VANILLA;
    }
}
