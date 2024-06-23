/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.chisel;

import com.falsepattern.rple.api.client.RPLEAOHelper;
import net.minecraft.client.renderer.RenderBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import team.chisel.ctmlib.RenderBlocksCTM;

@Mixin(RenderBlocksCTM.class)
public abstract class RenderBlocksCTMMixin extends RenderBlocks {
    /**
     * @author Ven
     * @reason Colorize
     */
    @Overwrite(remap = false)
    @SuppressWarnings("OverwriteModifiers")
    private int avg(int... brightnessValues) {
        switch (brightnessValues.length) {
            case 2:
                return RPLEAOHelper.average(false, brightnessValues[0], brightnessValues[1]);
            case 4:
                return RPLEAOHelper.average(false,
                                            brightnessValues[0],
                                            brightnessValues[1],
                                            brightnessValues[2],
                                            brightnessValues[3]);
            default:
                return RPLEAOHelper.average(false, brightnessValues);
        }
    }
}
