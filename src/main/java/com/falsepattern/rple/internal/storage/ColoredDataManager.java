/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.storage;

import com.falsepattern.chunk.api.ChunkDataManager;
import com.falsepattern.rple.internal.RPLE;
import com.falsepattern.rple.internal.Tags;
import lombok.AllArgsConstructor;
import lombok.val;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.Constants;

import java.nio.ByteBuffer;

@AllArgsConstructor
public final class ColoredDataManager implements ChunkDataManager.SectionNBTDataManager, ChunkDataManager.PacketDataManager {
    private static final int VERSION_HASH = Tags.VERSION.hashCode();

    private final int colorChannel;

    @Override
    public int maxPacketSize() {
        return 16 * 16 * 16 * 8 * 2 + 256;
    }

    @Override
    public void writeToBuffer(Chunk chunk, int ebsMask, boolean forceUpdate, ByteBuffer data) {
        val carrier = (ColoredCarrierChunk) chunk;
        val hasSky = !chunk.worldObj.provider.hasNoSky;
        val cn = carrier.getColoredChunk(colorChannel);
        for (int i = 0; i < 16; i++) {
            if ((ebsMask & (1 << i)) == 0)
                continue;

            val ebs = cn.lumiEBS(i);
            if (ebs == null)
                continue;

            data.put(ebs.lumiBlocklightArray().data);
            if (hasSky)
                data.put(ebs.lumiSkylightArray().data);
        }
    }

    @Override
    public void readFromBuffer(Chunk chunk, int ebsMask, boolean forceUpdate, ByteBuffer buffer) {
        val carrier = (ColoredCarrierChunk) chunk;
        val hasSky = !chunk.worldObj.provider.hasNoSky;
        val cn = carrier.getColoredChunk(colorChannel);
        for (int i = 0; i < 16; i++) {
            if ((ebsMask & (1 << i)) == 0)
                continue;

            val ebs = cn.lumiEBS(i);
            if (ebs == null)
                continue;

            buffer.get(ebs.lumiBlocklightArray().data);
            if (hasSky)
                buffer.get(ebs.lumiSkylightArray().data);
        }
    }

    @Override
    public void writeSectionToNBT(Chunk chunk, ExtendedBlockStorage ebs, NBTTagCompound section) {
        val cEbs = ((ColoredCarrierEBS) ebs).getColoredEBS(colorChannel);
        section.setInteger("v", VERSION_HASH);
        section.setByteArray("BlockLight", cEbs.lumiBlocklightArray().data);

        if (!chunk.worldObj.provider.hasNoSky)
            section.setByteArray("SkyLight", cEbs.lumiSkylightArray().data);
    }

    @Override
    public void readSectionFromNBT(Chunk chunk, ExtendedBlockStorage ebs, NBTTagCompound section) {
        val cEbs = ((ColoredCarrierEBS) ebs).getColoredEBS(colorChannel);

        if (!section.hasKey("v", Constants.NBT.TAG_INT))
            return;

        val ver = section.getInteger("v");
        if (ver != VERSION_HASH)
            return;

        safeNibbleArray(section, "BlockLight", cEbs.lumiBlocklightArray());
        if (!chunk.worldObj.provider.hasNoSky)
            safeNibbleArray(section, "SkyLight", cEbs.lumiSkylightArray());
    }

    private static void safeNibbleArray(NBTTagCompound compound, String key, NibbleArray array) {
        if (!compound.hasKey(key, Constants.NBT.TAG_BYTE_ARRAY))
            return;

        val arr = compound.getByteArray(key);
        if (arr.length == 2048)
            System.arraycopy(arr, 0, array.data, 0, 2048);
    }

    @Override
    public String domain() {
        return Tags.MODID + "_" + Tags.VERSION;
    }

    @Override
    public String id() {
        return RPLE.IDs[colorChannel];
    }
}
