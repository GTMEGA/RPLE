package com.falsepattern.rple.internal.storage;

import com.falsepattern.lumina.api.ILightingEngine;
import com.falsepattern.lumina.api.ILumiChunk;
import com.falsepattern.lumina.api.ILumiEBS;
import com.falsepattern.lumina.api.ILumiWorld;
import com.falsepattern.lumina.api.ILumiWorldRoot;
import com.falsepattern.lumina.internal.world.lighting.LightingEngine;
import com.falsepattern.lumina.internal.world.lighting.LightingHooks;
import com.falsepattern.rple.internal.Tags;
import lombok.Getter;
import lombok.Setter;

import net.minecraft.block.Block;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ColoredLightWorld implements ILumiWorld {
    @Getter
    @Setter
    private ILightingEngine lightingEngine;
    private final World carrier;
    public final ColoredLightChannel channel;
    private final String id;

    public ColoredLightWorld(World world, ColoredLightChannel channel) {
        this.carrier = world;
        this.channel = channel;
        id = Tags.MODID + "_" + channel.name();
    }

    @Override
    public ColoredLightChunk wrap(Chunk chunk) {
        return ((ColoredCarrierChunk)chunk).getColoredChunk(channel);
    }

    @Override
    public ColoredLightEBS wrap(ExtendedBlockStorage ebs) {
        return ((ColoredCarrierEBS)ebs).getColoredEBS(channel);
    }

    @Override
    public int getLightValueForState(Block state, int x, int y, int z) {
        return ((ColoredBlock)state).getColoredLightValue(carrier, channel, x, y, z);
    }

    @Override
    public int getLightOpacity(Block state, int x, int y, int z) {
        return ((ColoredBlock)state).getColoredLightOpacity(carrier, channel, x, y, z);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public ILumiWorldRoot root() {
        return (ILumiWorldRoot) carrier;
    }

    @SideOnly(Side.CLIENT)
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int minBlock) {
        int sky = this.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, x, y, z);
        int block = this.getSkyBlockTypeBrightness(EnumSkyBlock.Block, x, y, z);

        if (block < minBlock) {
            block = minBlock;
        }
        return (sky & 0xF) << 20 | (block & 0xF) << 4;
    }

    @SideOnly(Side.CLIENT)
    public int getSkyBlockTypeBrightness(EnumSkyBlock skyBlock, int x, int y, int z)
    {
        if (carrier.provider.hasNoSky && skyBlock == EnumSkyBlock.Sky) {
            return 0;
        } else {
            if (y < 0) {
                y = 0;
            }

            if (y >= 256) {
                return skyBlock.defaultLightValue;
            } else if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
                int l = x >> 4;
                int i1 = z >> 4;

                if (!carrier.chunkExists(l, i1))
                {
                    return skyBlock.defaultLightValue;
                }
                else if (carrier.getBlock(x, y, z).getUseNeighborBrightness())
                {
                    int j2 = this.getSavedLightValue(skyBlock, x, y + 1, z);
                    int j1 = this.getSavedLightValue(skyBlock, x + 1, y, z);
                    int k1 = this.getSavedLightValue(skyBlock, x - 1, y, z);
                    int l1 = this.getSavedLightValue(skyBlock, x, y, z + 1);
                    int i2 = this.getSavedLightValue(skyBlock, x, y, z - 1);

                    if (j1 > j2)
                    {
                        j2 = j1;
                    }

                    if (k1 > j2)
                    {
                        j2 = k1;
                    }

                    if (l1 > j2)
                    {
                        j2 = l1;
                    }

                    if (i2 > j2)
                    {
                        j2 = i2;
                    }

                    return j2;
                }
                else
                {
                    ColoredLightChunk chunk = wrap(carrier.getChunkFromChunkCoords(l, i1));
                    if(skyBlock == EnumSkyBlock.Block)
                        return getIntrinsicOrSavedBlockLightValue(chunk, x & 15, y, z & 15);
                    else
                        return chunk.getSavedLightValue(skyBlock, x & 15, y, z & 15);
                }
            }
            else
            {
                return skyBlock.defaultLightValue;
            }
        }
    }

    public int getSavedLightValue(EnumSkyBlock skyBlock, int x, int y, int z)
    {
        if (y < 0) {
            y = 0;
        }

        if (y >= 256) {
            y = 255;
        }

        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
            int l = x >> 4;
            int i1 = z >> 4;

            if (!carrier.chunkExists(l, i1))
            {
                return skyBlock.defaultLightValue;
            }
            else
            {
                ColoredLightChunk chunk = wrap(carrier.getChunkFromChunkCoords(l, i1));

                if(skyBlock == EnumSkyBlock.Block)
                    return getIntrinsicOrSavedBlockLightValue(chunk, x & 15, y, z & 15);
                else
                    return chunk.getSavedLightValue(skyBlock, x & 15, y, z & 15);
            }
        } else {
            return skyBlock.defaultLightValue;
        }
    }

    private int getIntrinsicOrSavedBlockLightValue(ColoredLightChunk chunk, int x, int y, int z) {
        int savedLightValue = chunk.getSavedLightValue(EnumSkyBlock.Block, x, y, z);
        int bx = x + (chunk.x() * 16);
        int bz = z + (chunk.z() * 16);
        Block block = chunk.root().getBlock(x, y, z);
        int lightValue = chunk.lumiWorld().getLightValueForState(block, bx, y, bz);
        return Math.max(savedLightValue, lightValue);
    }
}
