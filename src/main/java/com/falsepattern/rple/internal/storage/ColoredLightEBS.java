package com.falsepattern.rple.internal.storage;

import com.falsepattern.lumina.api.ILumiEBS;
import com.falsepattern.lumina.api.ILumiEBSRoot;
import com.falsepattern.rple.api.LightConstants;

import net.minecraft.block.Block;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class ColoredLightEBS implements ILumiEBS {
    private final ILumiEBSRoot carrier;
    private NibbleArray skylightArray;
    private NibbleArray blocklightArray;

    public ColoredLightEBS(int colorChannel, ILumiEBSRoot carrier, boolean hasSky) {
        this.carrier = carrier;
        if (colorChannel == LightConstants.COLOR_CHANNEL_RED) {
            blocklightArray = ((ILumiEBS)carrier).lumiBlocklightArray();
            skylightArray = ((ILumiEBS)carrier).lumiSkylightArray();
        } else {
            blocklightArray = new NibbleArray(4096, 4);
            if (hasSky) {
                skylightArray = new NibbleArray(4096, 4);
            }
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
