/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.lumina;

import com.falsepattern.lumina.api.ILumiEBSRoot;
import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.internal.common.helper.storage.ColoredCarrierEBS;
import com.falsepattern.rple.internal.common.helper.storage.ColoredLightEBS;
import lombok.val;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ExtendedBlockStorage.class)
public abstract class EBSMixin implements ColoredCarrierEBS {
    @Shadow private NibbleArray skylightArray;
    private ColoredLightEBS cRed;
    private ColoredLightEBS cGreen;
    private ColoredLightEBS cBlue;
    private boolean colorInit;

    @Override
    public ColoredLightEBS getColoredEBS(int colorChannel) {
        if (!colorInit) {
            initColoredEBS(this.skylightArray != null);
            colorInit = true;
        }
        switch (colorChannel) {
            case LightConstants.COLOR_CHANNEL_RED: return cRed;
            case LightConstants.COLOR_CHANNEL_GREEN: return cGreen;
            case LightConstants.COLOR_CHANNEL_BLUE: return cBlue;
            default: throw new IllegalArgumentException();
        }
    }

    private void initColoredEBS(boolean hasSky) {
        cRed = new ColoredLightEBS(LightConstants.COLOR_CHANNEL_RED, (ILumiEBSRoot) this, hasSky);
        cGreen = new ColoredLightEBS(LightConstants.COLOR_CHANNEL_GREEN, (ILumiEBSRoot) this, hasSky);
        cBlue = new ColoredLightEBS(LightConstants.COLOR_CHANNEL_BLUE, (ILumiEBSRoot) this, hasSky);
    }

    /**
     * @author FalsePattern
     * @reason Logic compat
     */
    @Overwrite
    public int getExtSkylightValue(int x, int y, int z) {
        val carr = (ColoredCarrierEBS) this;
        return max3(carr.getColoredEBS(LightConstants.COLOR_CHANNEL_RED).lumiSkylightArray(),
                    carr.getColoredEBS(LightConstants.COLOR_CHANNEL_GREEN).lumiSkylightArray(),
                    carr.getColoredEBS(LightConstants.COLOR_CHANNEL_BLUE).lumiSkylightArray(),
                    x, y, z);
    }

    /**
     * @author FalsePattern
     * @reason Logic compat
     */
    @Overwrite
    public int getExtBlocklightValue(int x, int y, int z) {
        val carr = (ColoredCarrierEBS) this;
        return max3(carr.getColoredEBS(LightConstants.COLOR_CHANNEL_RED).lumiBlocklightArray(),
                    carr.getColoredEBS(LightConstants.COLOR_CHANNEL_GREEN).lumiBlocklightArray(),
                    carr.getColoredEBS(LightConstants.COLOR_CHANNEL_BLUE).lumiBlocklightArray(),
                    x, y, z);
    }

    private static int max3(NibbleArray a, NibbleArray b, NibbleArray c, int x, int y, int z) {
        val A = a.get(x, y, z);
        val B = b.get(x, y, z);
        val C = c.get(x, y, z);
        return Math.max(A, Math.max(B, C));
    }
}
