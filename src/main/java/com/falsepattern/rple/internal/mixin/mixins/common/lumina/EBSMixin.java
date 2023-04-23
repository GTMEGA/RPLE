package com.falsepattern.rple.internal.mixin.mixins.common.lumina;

import com.falsepattern.lumina.api.ILumiEBSRoot;
import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.internal.storage.ColoredCarrierEBS;
import com.falsepattern.rple.internal.storage.ColoredLightEBS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

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
}
