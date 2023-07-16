/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.rple;

import com.falsepattern.lumina.api.world.LumiWorld;
import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.common.storage.world.RPLEWorld;
import com.falsepattern.rple.internal.common.storage.world.RPLEWorldRoot;
import com.falsepattern.rple.internal.common.storage.world.RPLEWorldWrapper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Unique
@Mixin(World.class)
public abstract class RPLEWorldRootImplMixin implements IBlockAccess, LumiWorld, RPLEWorldRoot {
    private RPLEWorld rple$redChannel = null;
    private RPLEWorld rple$greenChannel = null;
    private RPLEWorld rple$blueChannel = null;

    @Inject(method = "<init>*",
            at = @At("RETURN"),
            require = 2)
    private void rpleWorldInit() {
        this.rple$redChannel = new RPLEWorldWrapper();
        this.rple$greenChannel = new RPLEWorldWrapper();
        this.rple$blueChannel = new RPLEWorldWrapper();
    }

    @Override
    public RPLEWorld rple$world(ColorChannel channel) {
        switch (channel) {
            default:
            case RED_CHANNEL:
                return rple$redChannel;
            case GREEN_CHANNEL:
                return rple$greenChannel;
            case BLUE_CHANNEL:
                return rple$blueChannel;
        }
    }
}
