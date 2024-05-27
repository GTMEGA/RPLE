/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.rple;

import com.falsepattern.rple.internal.client.render.RGBHelper;
import com.falsepattern.rple.internal.client.storage.RPLEClientBlockStorage;
import com.falsepattern.rple.internal.client.storage.RPLEClientChunk;
import com.falsepattern.rple.internal.common.cache.RPLEBlockStorageRoot;
import lombok.val;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static com.falsepattern.rple.internal.client.render.RGBHelper.RGB_MAX_SKYLIGHT_NO_BLOCKLIGHT;
import static com.falsepattern.rple.internal.client.render.RGBHelper.RGB_NO_SKYLIGHT_NO_BLOCKLIGHT;


@Unique
@Mixin({World.class, ChunkCache.class})
public abstract class RPLEClientBlockStorageImplMixin implements RPLEClientBlockStorage, RPLEBlockStorageRoot {
    @Override
    public int rple$getRGBLightValue(boolean useNeighborValues, int posX, int posY, int posZ) {
        if (!lumi$hasSky())
            return rple$getRGBLightValueNoSky(useNeighborValues, posX, posY, posZ);

        if (!rple$worldBoundsCheck(posX, posY, posZ))
            return posY >= 0 ? RGB_MAX_SKYLIGHT_NO_BLOCKLIGHT : RGB_NO_SKYLIGHT_NO_BLOCKLIGHT;

        val centerChunkPosX = posX >> 4;
        val centerChunkPosZ = posZ >> 4;

        val centerChunk = rple$getClientFromChunkPosIfExists(centerChunkPosX, centerChunkPosZ);
        if (centerChunk == null)
            return RGB_MAX_SKYLIGHT_NO_BLOCKLIGHT;

        val centerSubChunkPosX = posX & 15;
        val centerSubChunkPosZ = posZ & 15;
        if (!useNeighborValues)
            return centerChunk.rple$getRGBLightValueHasSky(centerSubChunkPosX, posY, centerSubChunkPosZ);

        // Top and bottom, they will be in the same chunk.
        val lightValueYP = posY == 255 ? RGB_MAX_SKYLIGHT_NO_BLOCKLIGHT : centerChunk.rple$getRGBLightValueHasSky(centerSubChunkPosX, posY + 1, centerSubChunkPosZ);

        // Bottom is not used in vanilla code
//        val lightValueYN = posY == 0 ? PACKED_NO_SKYLIGHT_NO_BLOCKLIGHT : centerChunk.rple$getRGBLightValueHasSky(centerSubChunkPosX, posY - 1, centerSubChunkPosZ);

        final int lightValueXP;
        final int lightValueXN;
        if (centerSubChunkPosX == 0) {
            lightValueXP = centerChunk.rple$getRGBLightValueHasSky(1, posY, centerSubChunkPosZ);
            val chunkXN = rple$getClientFromChunkPosIfExists(centerChunkPosX - 1, centerChunkPosZ);
            if (chunkXN == null) {
                lightValueXN = RGB_MAX_SKYLIGHT_NO_BLOCKLIGHT;
            } else {
                lightValueXN = chunkXN.rple$getRGBLightValueHasSky(15, posY, centerSubChunkPosZ);
            }
        } else if (centerSubChunkPosX == 15) {
            val chunkXP = rple$getClientFromChunkPosIfExists(centerChunkPosX + 1, centerChunkPosZ);
            if (chunkXP == null) {
                lightValueXP = RGB_MAX_SKYLIGHT_NO_BLOCKLIGHT;
            } else {
                lightValueXP = chunkXP.rple$getRGBLightValueHasSky(0, posY, centerSubChunkPosZ);
            }
            lightValueXN = centerChunk.rple$getRGBLightValueHasSky(14, posY, centerSubChunkPosZ);
        } else {
            lightValueXP = centerChunk.rple$getRGBLightValueHasSky(centerSubChunkPosX + 1, posY, centerSubChunkPosZ);
            lightValueXN = centerChunk.rple$getRGBLightValueHasSky(centerSubChunkPosX - 1, posY, centerSubChunkPosZ);
        }

        final int lightValueZP;
        final int lightValueZN;
        if (centerSubChunkPosZ == 0) {
            lightValueZP = centerChunk.rple$getRGBLightValueHasSky(centerSubChunkPosX, posY, 1);
            val chunkXN = rple$getClientFromChunkPosIfExists(centerChunkPosX, centerChunkPosZ - 1);
            if (chunkXN == null) {
                lightValueZN = RGB_MAX_SKYLIGHT_NO_BLOCKLIGHT;
            } else {
                lightValueZN = chunkXN.rple$getRGBLightValueHasSky(centerSubChunkPosX, posY, 15);
            }
        } else if (centerSubChunkPosZ == 15) {
            val chunkXP = rple$getClientFromChunkPosIfExists(centerChunkPosX, centerChunkPosZ + 1);
            if (chunkXP == null) {
                lightValueZP = RGB_MAX_SKYLIGHT_NO_BLOCKLIGHT;
            } else {
                lightValueZP = chunkXP.rple$getRGBLightValueHasSky(centerSubChunkPosX, posY, 0);
            }
            lightValueZN = centerChunk.rple$getRGBLightValueHasSky(centerSubChunkPosX, posY, 14);
        } else {
            lightValueZP = centerChunk.rple$getRGBLightValueHasSky(centerSubChunkPosX, posY, centerSubChunkPosZ + 1);
            lightValueZN = centerChunk.rple$getRGBLightValueHasSky(centerSubChunkPosX, posY, centerSubChunkPosZ - 1);
        }

        return RGBHelper.rgbMax(lightValueYP,
                                lightValueXN,
                                lightValueXP,
                                lightValueZP,
                                lightValueZN);
    }

    @Override
    public int rple$getRGBLightValueNoSky(boolean useNeighborValues, int posX, int posY, int posZ) {
        if (!rple$worldBoundsCheck(posX, posY, posZ))
            return RGB_NO_SKYLIGHT_NO_BLOCKLIGHT;

        val centerChunkPosX = posX >> 4;
        val centerChunkPosZ = posZ >> 4;

        val centerChunk = rple$getClientFromChunkPosIfExists(centerChunkPosX, centerChunkPosZ);
        if (centerChunk == null)
            return RGB_NO_SKYLIGHT_NO_BLOCKLIGHT;

        val centerSubChunkPosX = posX & 15;
        val centerSubChunkPosZ = posZ & 15;
        if (!useNeighborValues)
            return centerChunk.rple$getRGBLightValueNoSky(centerSubChunkPosX, posY, centerSubChunkPosZ);

        // Top and bottom, they will be in the same chunk.
        val lightValueYP = posY == 255 ? RGB_NO_SKYLIGHT_NO_BLOCKLIGHT : centerChunk.rple$getRGBLightValueNoSky(centerSubChunkPosX, posY + 1, centerSubChunkPosZ);

        // Bottom is not used in vanilla code
//        val lightValueYN = posY == 0 ? 0x000_000 : centerChunk.rple$getRGBLightValueNoSky(centerSubChunkPosX, posY - 1, centerSubChunkPosZ);

        final int lightValueXP;
        final int lightValueXN;
        if (centerSubChunkPosX == 0) {
            lightValueXP = centerChunk.rple$getRGBLightValueNoSky(1, posY, centerSubChunkPosZ);
            val chunkXN = rple$getClientFromChunkPosIfExists(centerChunkPosX - 1, centerChunkPosZ);
            if (chunkXN == null) {
                lightValueXN = RGB_NO_SKYLIGHT_NO_BLOCKLIGHT;
            } else {
                lightValueXN = chunkXN.rple$getRGBLightValueNoSky(15, posY, centerSubChunkPosZ);
            }
        } else if (centerSubChunkPosX == 15) {
            val chunkXP = rple$getClientFromChunkPosIfExists(centerChunkPosX + 1, centerChunkPosZ);
            if (chunkXP == null) {
                lightValueXP = RGB_NO_SKYLIGHT_NO_BLOCKLIGHT;
            } else {
                lightValueXP = chunkXP.rple$getRGBLightValueNoSky(0, posY, centerSubChunkPosZ);
            }
            lightValueXN = centerChunk.rple$getRGBLightValueNoSky(14, posY, centerSubChunkPosZ);
        } else {
            lightValueXP = centerChunk.rple$getRGBLightValueNoSky(centerSubChunkPosX + 1, posY, centerSubChunkPosZ);
            lightValueXN = centerChunk.rple$getRGBLightValueNoSky(centerSubChunkPosX - 1, posY, centerSubChunkPosZ);
        }

        final int lightValueZP;
        final int lightValueZN;
        if (centerSubChunkPosZ == 0) {
            lightValueZP = centerChunk.rple$getRGBLightValueNoSky(centerSubChunkPosX, posY, 1);
            val chunkXN = rple$getClientFromChunkPosIfExists(centerChunkPosX, centerChunkPosZ - 1);
            if (chunkXN == null) {
                lightValueZN = RGB_NO_SKYLIGHT_NO_BLOCKLIGHT;
            } else {
                lightValueZN = chunkXN.rple$getRGBLightValueNoSky(centerSubChunkPosX, posY, 15);
            }
        } else if (centerSubChunkPosZ == 15) {
            val chunkXP = rple$getClientFromChunkPosIfExists(centerChunkPosX, centerChunkPosZ + 1);
            if (chunkXP == null) {
                lightValueZP = RGB_NO_SKYLIGHT_NO_BLOCKLIGHT;
            } else {
                lightValueZP = chunkXP.rple$getRGBLightValueNoSky(centerSubChunkPosX, posY, 0);
            }
            lightValueZN = centerChunk.rple$getRGBLightValueNoSky(centerSubChunkPosX, posY, 14);
        } else {
            lightValueZP = centerChunk.rple$getRGBLightValueNoSky(centerSubChunkPosX, posY, centerSubChunkPosZ + 1);
            lightValueZN = centerChunk.rple$getRGBLightValueNoSky(centerSubChunkPosX, posY, centerSubChunkPosZ - 1);
        }

        return RGBHelper.rgbMax(lightValueYP,
                                lightValueXN,
                                lightValueXP,
                                lightValueZP,
                                lightValueZN);
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
