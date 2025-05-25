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

import com.falsepattern.rple.api.client.ClientColorHelper;
import com.falsepattern.rple.internal.client.storage.RPLEClientSubChunk;
import com.falsepattern.rple.internal.common.chunk.RPLESubChunk;
import lombok.val;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Unique
@Mixin(ExtendedBlockStorage.class)
public abstract class RPLEClientSubChunkImplMixin implements RPLEClientSubChunk {
    @Dynamic("Initialized in: [com.falsepattern.rple.internal.mixin.mixins.common.rple.RPLESubChunkRootImplMixin]")
    private RPLESubChunk rple$redChannel;
    @Dynamic("Initialized in: [com.falsepattern.rple.internal.mixin.mixins.common.rple.RPLESubChunkRootImplMixin]")
    private RPLESubChunk rple$greenChannel;
    @Dynamic("Initialized in: [com.falsepattern.rple.internal.mixin.mixins.common.rple.RPLESubChunkRootImplMixin]")
    private RPLESubChunk rple$blueChannel;

    @Override
    public int rple$getRGBLightValueHasSky(int subChunkPosX, int subChunkPosY, int subChunkPosZ) {
        val skyR = rple$redChannel.lumi$getSkyLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
        val skyG = rple$greenChannel.lumi$getSkyLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
        val skyB = rple$blueChannel.lumi$getSkyLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
        val blkR = rple$redChannel.lumi$getBlockLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
        val blkG = rple$greenChannel.lumi$getBlockLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
        val blkB = rple$blueChannel.lumi$getBlockLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
        return ClientColorHelper.RGB32FromChannels4Bit(skyR, skyG, skyB, blkR, blkG, blkB);
    }

    @Override
    public int rple$getRGBLightValueNoSky(int subChunkPosX, int subChunkPosY, int subChunkPosZ) {
        val blkR = rple$redChannel.lumi$getBlockLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
        val blkG = rple$greenChannel.lumi$getBlockLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
        val blkB = rple$blueChannel.lumi$getBlockLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
        return ClientColorHelper.RGB32FromChannels4BitBlock(blkR, blkG, blkB);
    }
}
