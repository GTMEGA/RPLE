/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.storage;

import com.falsepattern.lumina.api.ILumiEBS;
import com.falsepattern.lumina.api.ILumiEBSRoot;
import com.falsepattern.rple.api.color.ColorChannel;
import net.minecraft.world.chunk.NibbleArray;
import org.jetbrains.annotations.Nullable;

import static com.falsepattern.rple.api.color.ColorChannel.RED_CHANNEL;

public final class ColoredLightEBS implements ILumiEBS {
    private final ILumiEBSRoot carrier;
    @Nullable
    private final NibbleArray skylightArray;
    private final NibbleArray blocklightArray;

    public ColoredLightEBS(ColorChannel channel, ILumiEBSRoot carrier, boolean hasSky) {
        this.carrier = carrier;

        if (channel == RED_CHANNEL) {
            blocklightArray = ((ILumiEBS) carrier).lumiBlocklightArray();
            skylightArray = ((ILumiEBS) carrier).lumiSkylightArray();
        } else {
            blocklightArray = new NibbleArray(4096, 4);
            if (hasSky) {
                skylightArray = new NibbleArray(4096, 4);
            } else {
                skylightArray = null;
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
