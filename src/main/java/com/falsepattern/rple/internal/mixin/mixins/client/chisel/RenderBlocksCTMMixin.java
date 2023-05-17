package com.falsepattern.rple.internal.mixin.mixins.client.chisel;

import com.falsepattern.rple.internal.color.BrightnessUtil;
import net.minecraft.client.renderer.RenderBlocks;
import org.spongepowered.asm.mixin.*;
import team.chisel.ctmlib.RenderBlocksCTM;

@Mixin(RenderBlocksCTM.class)
public abstract class RenderBlocksCTMMixin extends RenderBlocks {
    /**
     * @author Ven
     * @reason Cookie Support
     */
    @Overwrite(remap = false)
    private int avg(int... lightVals) {
        // TODO: This just makes all chisel things full-bright. Needs proper colour mixing fix.
        return BrightnessUtil.lightLevelsToBrightness(15,15);
    }
}
