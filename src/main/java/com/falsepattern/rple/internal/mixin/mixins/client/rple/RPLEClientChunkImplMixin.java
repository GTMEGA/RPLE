/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
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

package com.falsepattern.rple.internal.mixin.mixins.client.rple;

import com.falsepattern.rple.internal.client.storage.RPLEClientChunk;
import com.falsepattern.rple.internal.client.storage.RPLEClientSubChunk;
import lombok.val;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import static com.falsepattern.rple.api.client.ClientColorHelper.RGB32_MAX_SKYLIGHT_NO_BLOCKLIGHT;
import static com.falsepattern.rple.api.client.ClientColorHelper.RGB32_NO_SKYLIGHT_NO_BLOCKLIGHT;


@Unique
@Mixin(Chunk.class)
public abstract class RPLEClientChunkImplMixin implements RPLEClientChunk {
    @Shadow
    private ExtendedBlockStorage[] storageArrays;

    @Override
    public int rple$getRGBLightValueHasSky(int subChunkPosX, int posY, int subChunkPosZ) {
        val chunkPosY = (posY & 255) >> 4;
        val subChunk = rple$getClientSubChunkIfPrepared(chunkPosY);
        if (subChunk == null)
            return RGB32_MAX_SKYLIGHT_NO_BLOCKLIGHT;

        val subChunkPosY = posY & 15;
        return subChunk.rple$getRGBLightValueHasSky(subChunkPosX, subChunkPosY, subChunkPosZ);
    }

    @Override
    public int rple$getRGBLightValueNoSky(int subChunkPosX, int posY, int subChunkPosZ) {
        val chunkPosY = (posY & 255) >> 4;
        val subChunk = rple$getClientSubChunkIfPrepared(chunkPosY);
        if (subChunk == null)
            return RGB32_NO_SKYLIGHT_NO_BLOCKLIGHT;

        val subChunkPosY = posY & 15;
        return subChunk.rple$getRGBLightValueNoSky(subChunkPosX, subChunkPosY, subChunkPosZ);
    }

    @SuppressWarnings("InstanceofIncompatibleInterface")
    private @Nullable RPLEClientSubChunk rple$getClientSubChunkIfPrepared(int chunkPosY) {
        val subChunk = storageArrays[chunkPosY];
        if (subChunk instanceof RPLEClientSubChunk)
            return (RPLEClientSubChunk) subChunk;
        return null;
    }
}
