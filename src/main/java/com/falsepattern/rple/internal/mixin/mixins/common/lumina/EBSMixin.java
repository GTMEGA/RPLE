package com.falsepattern.rple.internal.mixin.mixins.common.lumina;

import com.falsepattern.lumina.api.ILumiEBSRoot;
import com.falsepattern.rple.internal.storage.ColoredCarrierEBS;
import com.falsepattern.rple.internal.storage.ColoredLightChannel;
import com.falsepattern.rple.internal.storage.ColoredLightChunk;
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
    public ColoredLightEBS getColoredEBS(ColoredLightChannel channel) {
        if (!colorInit) {
            initColoredEBS(this.skylightArray != null);
            colorInit = true;
        }
        switch (channel) {
            case RED: return cRed;
            case GREEN: return cGreen;
            case BLUE: return cBlue;
            default: throw new IllegalArgumentException();
        }
    }

    private void initColoredEBS(boolean hasSky) {
        cRed = new ColoredLightEBS((ILumiEBSRoot) this, hasSky);
        cGreen = new ColoredLightEBS((ILumiEBSRoot) this, hasSky);
        cBlue = new ColoredLightEBS((ILumiEBSRoot) this, hasSky);
    }
}
