package com.falsepattern.rple.internal.mixin.mixins.client.chisel;

import com.falsepattern.rple.internal.color.BrightnessUtil;
import net.minecraft.client.renderer.RenderBlocks;

import com.falsepattern.rple.internal.color.CookieMonster;
import com.falsepattern.rple.internal.color.CookieWrappers;
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
    private int avg(int... vals) {
        switch (vals.length) {
            case 2:
                return CookieWrappers.average(false, vals[0], vals[1]);
            case 4:
                return CookieWrappers.average(false, vals[0], vals[1], vals[2], vals[3]);
            default:
                return CookieWrappers.average(false, vals);
        }
    }
}
