package com.falsepattern.rple.internal.mixin.mixins.client.chisel;

import com.falsepattern.rple.internal.color.BrightnessUtil;
import net.minecraft.client.renderer.RenderBlocks;

import com.falsepattern.rple.internal.color.CookieMonster;
import lombok.val;
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
        val len = lightVals.length;
        long[] buffer = new long[len];
        for (int i = 0; i < len; i++) {
            buffer[i] = CookieMonster.cookieToPackedLong(lightVals[i]);
        }
        return CookieMonster.packedLongToCookie(BrightnessUtil.packedAverage(buffer, len, false));
    }
}
