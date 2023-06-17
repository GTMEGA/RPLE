/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.mixin.interfaces.ITessellatorJunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.renderer.Tessellator;

import java.nio.ShortBuffer;

@Mixin(Tessellator.class)
public abstract class Tessellator_NonOptiFineMixin implements ITessellatorJunction {
    @Shadow
    private static ShortBuffer shortBuffer;

    @Override
    public ShortBuffer RPLEgetShortBuffer() {
        return shortBuffer;
    }
}
