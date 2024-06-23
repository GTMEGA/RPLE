/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
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
