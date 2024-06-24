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

package com.falsepattern.rple.internal.mixin.mixins.common;

import com.falsepattern.rple.internal.common.chunk.RPLEChunkRoot;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.chunk.Chunk;

import static com.falsepattern.rple.api.common.color.ColorChannel.BLUE_CHANNEL;
import static com.falsepattern.rple.api.common.color.ColorChannel.GREEN_CHANNEL;
import static com.falsepattern.rple.api.common.color.ColorChannel.RED_CHANNEL;

@Mixin(Chunk.class)
public abstract class ChunkMixin implements RPLEChunkRoot {
    /**
     * @author FalsePattern
     * @reason Behaviour fix
     */
    @Overwrite
    public int getHeightValue(int x, int z) {
        val red = this.rple$chunk(RED_CHANNEL).lumi$skyLightHeight(x, z);
        val green = this.rple$chunk(GREEN_CHANNEL).lumi$skyLightHeight(x, z);
        val blue = this.rple$chunk(BLUE_CHANNEL).lumi$skyLightHeight(x, z);

        // Select whichever can reach the lowest height
        return Math.min(Math.min(red, green), blue);
    }

    /**
     * @author FalsePattern
     * @reason Behaviour fix
     */
    @Overwrite
    public boolean canBlockSeeTheSky(int x, int y, int z) {
        val red = this.rple$chunk(RED_CHANNEL).lumi$skyLightHeight(x, z);
        val green = this.rple$chunk(GREEN_CHANNEL).lumi$skyLightHeight(x, z);
        val blue = this.rple$chunk(BLUE_CHANNEL).lumi$skyLightHeight(x, z);

        return y >= red || y >= green || y >= blue;

    }
}
