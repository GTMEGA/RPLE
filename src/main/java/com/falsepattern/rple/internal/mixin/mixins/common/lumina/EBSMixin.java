/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.lumina;

import com.falsepattern.lumina.api.ILumiEBSRoot;
import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.common.storage.ColoredCarrierEBS;
import com.falsepattern.rple.internal.common.storage.ColoredLightEBS;
import lombok.val;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static com.falsepattern.rple.api.color.ColorChannel.*;

@Mixin(ExtendedBlockStorage.class)
public abstract class EBSMixin implements ColoredCarrierEBS {
    @Shadow
    private NibbleArray skylightArray;

    @Nullable
    private ColoredLightEBS cRed = null;
    @Nullable
    private ColoredLightEBS cGreen = null;
    @Nullable
    private ColoredLightEBS cBlue = null;

    private boolean colorInit;

    @Override
    public ColoredLightEBS getColoredEBS(ColorChannel channel) {
        if (!colorInit) {
            initColoredEBS(this.skylightArray != null);
            colorInit = true;
        }

        switch (channel) {
            default:
            case RED_CHANNEL:
                return cRed;
            case GREEN_CHANNEL:
                return cGreen;
            case BLUE_CHANNEL:
                return cBlue;
        }
    }

    private void initColoredEBS(boolean hasSky) {
        val thiz = (ILumiEBSRoot) this;

        this.cRed = new ColoredLightEBS(RED_CHANNEL, thiz, hasSky);
        this.cGreen = new ColoredLightEBS(GREEN_CHANNEL, thiz, hasSky);
        this.cBlue = new ColoredLightEBS(BLUE_CHANNEL, thiz, hasSky);
    }

    /**
     * @author FalsePattern
     * @reason Logic compat
     */
    @Overwrite
    public int getExtSkylightValue(int x, int y, int z) {
        val redLightArray = getColoredEBS(RED_CHANNEL).lumiSkylightArray();
        val greenLightArray = getColoredEBS(GREEN_CHANNEL).lumiSkylightArray();
        val blueLightArray = getColoredEBS(BLUE_CHANNEL).lumiSkylightArray();

        return max3(redLightArray, greenLightArray, blueLightArray, x, y, z);
    }

    /**
     * @author FalsePattern
     * @reason Logic compat
     */
    @Overwrite
    public int getExtBlocklightValue(int x, int y, int z) {
        val redLightArray = getColoredEBS(RED_CHANNEL).lumiBlocklightArray();
        val greenLightArray = getColoredEBS(GREEN_CHANNEL).lumiBlocklightArray();
        val blueLightArray = getColoredEBS(BLUE_CHANNEL).lumiBlocklightArray();

        return max3(redLightArray, greenLightArray, blueLightArray, x, y, z);
    }

    private static int max3(NibbleArray a, NibbleArray b, NibbleArray c, int x, int y, int z) {
        val A = a.get(x, y, z);
        val B = b.get(x, y, z);
        val C = c.get(x, y, z);
        return Math.max(A, Math.max(B, C));
    }
}
