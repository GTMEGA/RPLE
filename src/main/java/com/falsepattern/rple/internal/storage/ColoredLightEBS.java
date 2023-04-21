package com.falsepattern.rple.internal.storage;

import com.falsepattern.lumina.api.ILumiEBS;
import com.falsepattern.lumina.api.ILumiEBSRoot;

import net.minecraft.block.Block;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class ColoredLightEBS implements ILumiEBS {
    private final ILumiEBSRoot carrier;
    private NibbleArray skylightArray;
    private NibbleArray blocklightArray;

    public ColoredLightEBS(ILumiEBSRoot carrier, boolean hasSky) {
        this.carrier = carrier;
        blocklightArray = new NibbleArray(4096, 4);
        if (hasSky) {
            skylightArray = new NibbleArray(4096, 4);
        }
    }

    @Override
    public NibbleArray lumiSkylightArray() {
        return skylightArray;
    }

    @Override
    public NibbleArray lumiBlocklightArray() {
        return blocklightArray;
    }

    @Override
    public ILumiEBSRoot root() {
        return carrier;
    }
}
