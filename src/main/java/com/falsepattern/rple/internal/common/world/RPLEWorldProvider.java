/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.world;

import com.falsepattern.lumina.api.world.LumiWorld;
import com.falsepattern.lumina.api.world.LumiWorldProvider;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.Tags;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.falsepattern.rple.api.common.color.ColorChannel.*;

@Accessors(fluent = true, chain = false)
public final class RPLEWorldProvider implements LumiWorldProvider {
    private final ColorChannel channel;
    @Getter
    private final String worldProviderID;

    private static final RPLEWorldProvider RED_CHANNEL_INSTANCE = new RPLEWorldProvider(RED_CHANNEL);
    private static final RPLEWorldProvider GREEN_CHANNEL_INSTANCE = new RPLEWorldProvider(GREEN_CHANNEL);
    private static final RPLEWorldProvider BLUE_CHANNEL_INSTANCE = new RPLEWorldProvider(BLUE_CHANNEL);

    private RPLEWorldProvider(ColorChannel channel) {
        this.channel = channel;
        this.worldProviderID = Tags.MOD_ID + "_" + channel + "_world_provider";
    }

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
    @SuppressWarnings("InstanceofIncompatibleInterface")
    public @Nullable LumiWorld provideWorld(@Nullable World worldBase) {
        if (!(worldBase instanceof RPLEWorldRoot))
            return null;
        val worldRoot = (RPLEWorldRoot) worldBase;
        return worldRoot.rple$world(channel);
    }
}
