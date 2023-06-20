/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.helper.storage;

import com.falsepattern.lumina.api.*;
import com.falsepattern.rple.api.LightConstants;
import lombok.val;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;

public final class ColoredLightChunk implements ILumiChunk {
    private final int colorChannel;
    private final ColoredLightWorld world;
    private final Chunk carrier;

    private final int[] heightMap;
    private final boolean[] updateSkylightColumns;
    private int heightMapMinimum;
    private ILightingEngine lightingEngine;
    private short[] neighborLightChecks;
    private boolean isLightInitialized;

    public ColoredLightChunk(ColoredLightWorld world, Chunk carrier) {
        this.colorChannel = world.colorChannel;
        this.world = world;
        this.carrier = carrier;

        if (world.colorChannel == LightConstants.COLOR_CHANNEL_RED) {
            this.updateSkylightColumns = carrier.updateSkylightColumns;
            this.heightMap = carrier.heightMap;
        } else {
            this.updateSkylightColumns = new boolean[256];
            this.heightMap = new int[256];
        }
    }

    @Override
    public ILumiEBS lumiEBS(int arrayIndex) {
        val ebs = carrier.getBlockStorageArray()[arrayIndex];
        if (ebs == null)
            return null;

        return ((ColoredCarrierEBS) ebs).getColoredEBS(colorChannel);
    }

    @Override
    public ILumiWorld lumiWorld() {
        return world;
    }

    @Override
    public int[] lumiHeightMap() {
        return heightMap;
    }

    @Override
    public short[] lumiGetNeighborLightChecks() {
        return neighborLightChecks;
    }

    @Override
    public void lumiGetNeighborLightChecks(short[] data) {
        neighborLightChecks = data;
    }

    @Override
    public boolean lumiIsLightInitialized() {
        return isLightInitialized;
    }

    @Override
    public void lumiIsLightInitialized(boolean val) {
        isLightInitialized = val;
    }

    @Override
    public boolean[] lumiUpdateSkylightColumns() {
        return updateSkylightColumns;
    }

    @Override
    public int lumiHeightMapMinimum() {
        return heightMapMinimum;
    }

    @Override
    public void lumiHeightMapMinimum(int min) {
        heightMapMinimum = min;
    }

    @Override
    public ILumiChunkRoot root() {
        return (ILumiChunkRoot) carrier;
    }

    @Override
    public int x() {
        return carrier.xPosition;
    }

    @Override
    public int z() {
        return carrier.zPosition;
    }

    @Override
    public ILightingEngine getLightingEngine() {
        if (lightingEngine == null) {
            this.lightingEngine = world.getLightingEngine();
            if (lightingEngine == null)
                throw new IllegalStateException(); // TODO: [PRE_RELEASE] Can we avoid the throw here somehow?
        }

        return lightingEngine;
    }

    public int getSavedLightValue(EnumSkyBlock skyBlock, int x, int y, int z) {
        val ebs = lumiEBS(y >> 4);

        if (ebs == null) {
            if (carrier.canBlockSeeTheSky(x, y, z))
                return skyBlock.defaultLightValue;
            return 0;
        }

        if (skyBlock == EnumSkyBlock.Sky) {
            if (carrier.worldObj.provider.hasNoSky)
                return 0;
            return ebs.lumiSkylightArray().get(x, y & 15, z);
        }

        if (skyBlock == EnumSkyBlock.Block)
            return ebs.lumiBlocklightArray().get(x, y & 15, z);
        return skyBlock.defaultLightValue;
    }
}
