/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
 */

package com.falsepattern.rple.internal.common.world;

import com.falsepattern.lumi.api.world.LumiWorld;
import com.falsepattern.lumi.api.world.LumiWorldProvider;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.Tags;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.falsepattern.rple.api.common.color.ColorChannel.*;
import static com.falsepattern.rple.internal.Tags.MOD_ID;

@Accessors(fluent = true, chain = false)
public final class RPLEWorldProvider implements LumiWorldProvider {
    private static final String RED_WORLD_PROVIDER_ID = MOD_ID + "_" + RED_CHANNEL + "_world_provider";
    private static final String GREEN_WORLD_PROVIDER_ID = MOD_ID + "_" + GREEN_CHANNEL + "_world_provider";
    private static final String BLUE_WORLD_PROVIDER_ID = MOD_ID + "_" + BLUE_CHANNEL + "_world_provider";

    private final ColorChannel channel;
    @Getter
    private String worldProviderVersion;

    private static final RPLEWorldProvider RED_CHANNEL_INSTANCE = new RPLEWorldProvider(RED_CHANNEL);
    private static final RPLEWorldProvider GREEN_CHANNEL_INSTANCE = new RPLEWorldProvider(GREEN_CHANNEL);
    private static final RPLEWorldProvider BLUE_CHANNEL_INSTANCE = new RPLEWorldProvider(BLUE_CHANNEL);

    private RPLEWorldProvider(ColorChannel channel) {
        this.channel = channel;
        this.worldProviderVersion = Tags.VERSION + "_0x00000000";
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
    public @NotNull String worldProviderID() {
        switch (channel) {
            default:
            case RED_CHANNEL:
                return RED_WORLD_PROVIDER_ID;
            case GREEN_CHANNEL:
                return GREEN_WORLD_PROVIDER_ID;
            case BLUE_CHANNEL:
                return BLUE_WORLD_PROVIDER_ID;
        }
    }

    @Override
    @SuppressWarnings("InstanceofIncompatibleInterface")
    public @Nullable LumiWorld provideWorld(@Nullable World worldBase) {
        if (!(worldBase instanceof RPLEWorldRoot))
            return null;
        val worldRoot = (RPLEWorldRoot) worldBase;
        return worldRoot.rple$world(channel);
    }

    public void applyConfigHashCode(String configHashCode) {
        worldProviderVersion = Tags.VERSION + "_" + configHashCode;
    }
}
