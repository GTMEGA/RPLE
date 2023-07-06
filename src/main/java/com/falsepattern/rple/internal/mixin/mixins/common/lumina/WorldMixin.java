/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.lumina;

import com.falsepattern.lumina.api.world.LumiWorld;
import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.common.storage.RPLEWorld;
import com.falsepattern.rple.internal.common.storage.RPLEWorldRoot;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import sun.tools.jar.resources.jar;

import static com.falsepattern.rple.api.color.ColorChannel.*;

@Mixin(World.class)
public abstract class WorldMixin implements IBlockAccess, LumiWorld, RPLEWorldRoot {
    @Nullable
    private RPLEWorld redChannelWorld = null;
    @Nullable
    private RPLEWorld greenChannelWorld = null;
    @Nullable
    private RPLEWorld blueChannelWorld = null;

    private boolean colorInit = false;

    @Override
    public RPLEWorld rpleWorld(ColorChannel channel) {
        if (!colorInit) {
            rpleWorldInit();
            colorInit = true;
        }

        switch (channel) {
            default:
            case RED_CHANNEL:
                return redChannelWorld;
            case GREEN_CHANNEL:
                return greenChannelWorld;
            case BLUE_CHANNEL:
                return blueChannelWorld;
        }
    }

    private void rpleWorldInit() {
        redChannelWorld = new RPLEWorld(this, RED_CHANNEL);
        greenChannelWorld = new RPLEWorld(this, GREEN_CHANNEL);
        blueChannelWorld = new RPLEWorld(this, BLUE_CHANNEL);
    }
}
