/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.mixin.hook.ColoredLightingHooks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(World.class)
public abstract class WorldMixin implements IBlockAccess {
    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public int getLightBrightnessForSkyBlocks(int posX, int posY, int posZ, int cookieMinBlockLight) {
        return ColoredLightingHooks.getRGBBrightnessForTessellator(thiz(), posX, posY, posZ, cookieMinBlockLight);
    }

    private World thiz() {
        return (World) (Object) this;
    }
}
