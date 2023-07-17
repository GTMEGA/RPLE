/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.storage.world;

import com.falsepattern.lumina.api.world.LumiWorld;
import com.falsepattern.lumina.api.world.LumiWorldProvider;
import com.falsepattern.rple.api.color.ColorChannel;
import lombok.AllArgsConstructor;
import lombok.val;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.falsepattern.rple.api.color.ColorChannel.*;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public final class RPLEWorldProvider implements LumiWorldProvider {
    private final ColorChannel channel;

    private static final RPLEWorldProvider RED_CHANNEL_INSTANCE = new RPLEWorldProvider(RED_CHANNEL);
    private static final RPLEWorldProvider GREEN_CHANNEL_INSTANCE = new RPLEWorldProvider(GREEN_CHANNEL);
    private static final RPLEWorldProvider BLUE_CHANNEL_INSTANCE = new RPLEWorldProvider(BLUE_CHANNEL);

    public static RPLEWorldProvider redRPLEWorldProvider() {
        return RED_CHANNEL_INSTANCE;
    }

    public static RPLEWorldProvider greenRPLEWorldProvider() {
        return GREEN_CHANNEL_INSTANCE;
    }

    public static RPLEWorldProvider blueRPLEWorldProvider() {
        return BLUE_CHANNEL_INSTANCE;
    }

    @Override
    public @Nullable LumiWorld provideWorld(@NotNull World worldBase) {
        val worldRoot = (RPLEWorldRoot) worldBase;
        return worldRoot.rple$world(channel);
    }
}
