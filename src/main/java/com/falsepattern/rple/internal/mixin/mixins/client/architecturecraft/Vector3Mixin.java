/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.architecturecraft;

import com.falsepattern.rple.internal.mixin.interfaces.architecturecraft.IVector3Mixin;
import gcewing.architecture.Vector3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Unique
@Mixin(value = Vector3.class, remap = false)
public abstract class Vector3Mixin implements IVector3Mixin {
    @Shadow
    double x;
    @Shadow
    double y;
    @Shadow
    double z;

    @Override
    public double rple$posX() {
        return x;
    }

    @Override
    public double rple$posY() {
        return y;
    }

    @Override
    public double rple$posZ() {
        return z;
    }
}
