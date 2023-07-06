/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.storage;

import com.falsepattern.lumina.api.chunk.LumiChunk;
import com.falsepattern.lumina.api.chunk.LumiChunkRoot;
import com.falsepattern.lumina.api.engine.LumiLightingEngine;
import com.falsepattern.lumina.api.world.LumiWorld;
import com.falsepattern.rple.api.color.ColorChannel;
import lombok.val;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

public final class RPLEChunk implements LumiChunk {
    private final LumiChunk delegate;
    private final RPLEWorld world;
    private final ColorChannel channel;

    private final int[] skylightColumnHeightArray;
    private final boolean[] outdatedSkylightColumns;

    private int minSkylightColumnHeight;
    private short @Nullable [] neighborLightChecks;
    private boolean lightInitialized;

    private LumiLightingEngine lightingEngine;

    public RPLEChunk(LumiChunk delegate,
                     RPLEWorld world,
                     int[] skylightColumnHeightArray,
                     boolean[] outdatedSkylightColumns) {
        this.delegate = delegate;
        this.world = world;
        this.channel = world.channel();

        this.skylightColumnHeightArray = skylightColumnHeightArray;
        this.outdatedSkylightColumns = outdatedSkylightColumns;

        this.minSkylightColumnHeight = Integer.MAX_VALUE;
        this.neighborLightChecks = null;
        this.lightInitialized = false;

        this.lightingEngine = null;
    }

    public RPLEChunk(LumiChunk delegate,RPLEWorld world) {
        this.delegate = delegate;
        this.world = world;
        this.channel = world.channel();

        this.skylightColumnHeightArray = new int[256];
        this.outdatedSkylightColumns = new boolean[256];

        this.minSkylightColumnHeight = Integer.MAX_VALUE;
        this.neighborLightChecks = null;
        this.lightInitialized = false;

        this.lightingEngine = null;
    }

    @Override
    public int chunkPosX() {
        return delegate.chunkPosX();
    }

    @Override
    public int chunkPosZ() {
        return delegate.chunkPosZ();
    }

    @Override
    public @Nullable RPLESubChunk subChunk(int index) {
        val subChunk = delegate.subChunk(index);
        if (subChunk == null)
            return null;

        val rpleSubChunk = (RPLESubChunkRoot) subChunk;
        return rpleSubChunk.rpleSubChunk(channel);
    }

    @Override
    public LumiWorld lumiWorld() {
        return world;
    }

    @Override
    public void minSkylightColumnHeight(int minSkylightColumnHeight) {
        this.minSkylightColumnHeight = minSkylightColumnHeight;
    }

    @Override
    public int minSkylightColumnHeight() {
        return minSkylightColumnHeight;
    }

    @Override
    public int[] skylightColumnHeightArray() {
        return skylightColumnHeightArray;
    }

    @Override
    public void neighborLightChecks(short[] neighborLightChecks) {
        this.neighborLightChecks = neighborLightChecks;
    }

    @Override
    public short[] neighborLightChecks() {
        return neighborLightChecks;
    }

    @Override
    public void lightInitialized(boolean lightInitialized) {
        this.lightInitialized = lightInitialized;
    }

    @Override
    public boolean lightInitialized() {
        return lightInitialized;
    }

    @Override
    public boolean[] outdatedSkylightColumns() {
        return outdatedSkylightColumns;
    }

    @Override
    public LumiChunkRoot chunkRoot() {
        return delegate.chunkRoot();
    }

    @Override
    public LumiLightingEngine lightingEngine() {
        if (lightingEngine == null) {
            lightingEngine = world.lightingEngine();
            if (lightingEngine == null)
                throw new IllegalStateException(); // TODO: [PRE_RELEASE] Can we avoid the throw here somehow?
        }
        return lightingEngine;
    }

    public int getSavedLightValue(EnumSkyBlock lightType, int subChunkPosX, int posY, int subChunkPosZ) {
        val subChunk = subChunk(posY >> 4);

        if (subChunk == null) {
            if (((Chunk)delegate).canBlockSeeTheSky(subChunkPosX, posY, subChunkPosZ))
                return lightType.defaultLightValue;
            return 0;
        }

        if (lightType == EnumSkyBlock.Sky) {
            if (world.worldRoot().hasSkyLight())
                return subChunk.skyLight().get(subChunkPosX, posY & 15, subChunkPosZ);
            return 0;
        }

        if (lightType == EnumSkyBlock.Block)
            return subChunk.blockLight().get(subChunkPosX, posY & 15, subChunkPosZ);
        return lightType.defaultLightValue;
    }
}
