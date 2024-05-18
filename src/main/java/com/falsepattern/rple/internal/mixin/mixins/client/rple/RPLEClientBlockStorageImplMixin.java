/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.rple;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.rple.internal.client.storage.RPLEClientBlockStorage;
import com.falsepattern.rple.internal.client.storage.RPLEClientChunk;
import com.falsepattern.rple.internal.common.cache.RPLEBlockStorageRoot;
import lombok.val;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static com.falsepattern.lumina.api.lighting.LightType.BLOCK_LIGHT_TYPE;
import static com.falsepattern.rple.internal.common.color.ColorPackingUtil.packedLightValueRGBMax;

@Unique
@Mixin({World.class, ChunkCache.class})
public abstract class RPLEClientBlockStorageImplMixin implements RPLEClientBlockStorage, RPLEBlockStorageRoot {
    @Override
    public int rple$getRGBLightValue(boolean useNeighborValues, int posX, int posY, int posZ) {
        if (!lumi$hasSky())
            return rple$getRGBLightValue(useNeighborValues, BLOCK_LIGHT_TYPE, posX, posY, posZ);

        // NOTE: Bounds checks behave this way if the SKY exists.
        // If the Sky does not exist in this world, then the result should always be 0x00000000
        if (!rple$worldBoundsCheck(posX, posY, posZ))
            return posY >= 0 ? 0x00FFF000 : 0x00000000;

        val centerChunkPosX = posX >> 4;
        val centerChunkPosZ = posZ >> 4;

        val centerChunk = rple$getClientFromChunkPosIfExists(centerChunkPosX, centerChunkPosZ);
        if (centerChunk == null)
            return 0x00FFF000;

        val centerSubChunkPosX = posX & 15;
        val centerSubChunkPosZ = posZ & 15;
        if (!useNeighborValues)
            return centerChunk.rple$getRGBLightValue(centerSubChunkPosX, posY, centerSubChunkPosZ);

        // Top and bottom, they will be in the same chunk.
        val lightValueYP = centerChunk.rple$getRGBLightValue(centerSubChunkPosX, posY + 1, centerSubChunkPosZ);
        val lightValueYN = centerChunk.rple$getRGBLightValue(centerSubChunkPosX, posY - 1, centerSubChunkPosZ);

        final int lightValueXP;
        final int lightValueXN;
        // TODO: Validate the weird shit here
        if (centerSubChunkPosX == 0) {
            lightValueXP = centerChunk.rple$getRGBLightValue(1, posY, centerSubChunkPosZ);
            val chunkXN = rple$getClientFromChunkPosIfExists(centerChunkPosX - 1, centerChunkPosZ);
            if (chunkXN == null) {
                lightValueXN = 0x00FFF000;
            } else {
                lightValueXN = chunkXN.rple$getRGBLightValue(15, posY, centerSubChunkPosZ);
            }
        } else if (centerSubChunkPosX == 15) {
            val chunkXP = rple$getClientFromChunkPosIfExists(centerChunkPosX + 1, centerChunkPosZ);
            if (chunkXP == null) {
                lightValueXP = 0x00FFF000;
            } else {
                lightValueXP = chunkXP.rple$getRGBLightValue(0, posY, centerSubChunkPosZ);
            }
            lightValueXN = centerChunk.rple$getRGBLightValue(14, posY, centerSubChunkPosZ);
        } else {
            lightValueXP = centerChunk.rple$getRGBLightValue(centerSubChunkPosX + 1, posY, centerSubChunkPosZ);
            lightValueXN = centerChunk.rple$getRGBLightValue(centerSubChunkPosX - 1, posY, centerSubChunkPosZ);
        }

        final int lightValueZP;
        final int lightValueZN;
        // TODO: Validate the weird shit here
        if (centerSubChunkPosZ == 0) {
            lightValueZP = centerChunk.rple$getRGBLightValue(centerChunkPosX, posY, 1);
            val chunkXN = rple$getClientFromChunkPosIfExists(centerChunkPosX, centerChunkPosZ - 1);
            if (chunkXN == null) {
                lightValueZN = 0x00FFF000;
            } else {
                lightValueZN = chunkXN.rple$getRGBLightValue(centerChunkPosX, posY, 15);
            }
        } else if (centerSubChunkPosZ == 15) {
            val chunkXP = rple$getClientFromChunkPosIfExists(centerChunkPosX, centerChunkPosZ + 1);
            if (chunkXP == null) {
                lightValueZP = 0x00FFF000;
            } else {
                lightValueZP = chunkXP.rple$getRGBLightValue(centerChunkPosX, posY, 0);
            }
            lightValueZN = centerChunk.rple$getRGBLightValue(centerChunkPosX, posY, 14);
        } else {
            lightValueZP = centerChunk.rple$getRGBLightValue(centerSubChunkPosX, posY, centerSubChunkPosZ + 1);
            lightValueZN = centerChunk.rple$getRGBLightValue(centerSubChunkPosX, posY, centerSubChunkPosZ - 1);
        }

        return packedLightValueRGBMax(lightValueYP,
                                      lightValueYN,
                                      lightValueXN,
                                      lightValueXP,
                                      lightValueZP,
                                      lightValueZN);
    }

    @Override
    public int rple$getRGBLightValue(boolean useNeighborValues, LightType type, int posX, int posY, int posZ) {
        return 0;// TODO: Currently no-ops, this means the nether will be rather dim
    }

    @SuppressWarnings("InstanceofIncompatibleInterface")
    private @Nullable RPLEClientChunk rple$getClientFromChunkPosIfExists(int chunkPosX, int chunkPosZ) {
        val chunk = rple$getChunkRootFromChunkPosIfExists(chunkPosX, chunkPosZ);
        if (chunk instanceof RPLEClientChunk)
            return (RPLEClientChunk) chunk;
        return null;
    }

    private static boolean rple$worldBoundsCheck(int posX, int posY, int posZ) {
        check:
        {
            if (posX < -30_000_000 || posX > 30_000_000)
                break check;
            if (posZ < -30_000_000 || posZ > 30_000_000)
                break check;
            return posY >= 0 && posY <= 255;
        }
        return false;
    }
}
