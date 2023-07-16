/*
 * Copyright (c) 2023 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
