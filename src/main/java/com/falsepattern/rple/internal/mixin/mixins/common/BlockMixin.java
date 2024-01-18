/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common;

import com.falsepattern.rple.internal.mixin.hook.ColoredLightingHooks;
import com.falsepattern.rple.internal.mixin.interfaces.RPLERenamedBlockLightMethods;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
@SuppressWarnings("unused")
public abstract class BlockMixin implements RPLERenamedBlockLightMethods {
    @Shadow
    protected int lightValue;
    @Shadow
    protected int lightOpacity;

    /**
     * @author _
     * @reason _
     */
    @Overwrite
    public int getLightValue() {
//        return 0;
        return ColoredLightingHooks.getLightValue(thiz());
    }

    /**
     * @author _
     * @reason _
     */
    @Overwrite(remap = false)
    public int getLightValue(IBlockAccess world, int posX, int posY, int posZ) {
//        return 0;
        return ColoredLightingHooks.getLightValue(world, thiz(), posX, posY, posZ);
    }

    /**
     * @author _
     * @reason _
     */
    @Overwrite
    public int getLightOpacity() {
        return ColoredLightingHooks.getLightOpacity(thiz());
//        return thiz() == Blocks.air ? 0 : 15;
    }

    /**
     * @author _
     * @reason _
     */
    @Overwrite(remap = false)
    public int getLightOpacity(IBlockAccess world, int posX, int posY, int posZ) {
        return ColoredLightingHooks.getLightOpacity(world, thiz(), posX, posY, posZ);
//        return thiz() == Blocks.air ? 0 : 15;
    }

    @Override
    public int rple$renamed$getLightValue() {
        return this.lightValue;
    }

    @Override
    public int rple$renamed$getLightValue(IBlockAccess world, int posX, int posY, int posZ) {
        return this.lightValue;
    }

    @Override
    public int rple$renamed$getLightOpacity() {
        return this.lightOpacity;
    }

    @Override
    public int rple$renamed$getLightOpacity(IBlockAccess world, int posX, int posY, int posZ) {
        return this.lightOpacity;
    }

    private Block thiz() {
        return (Block) (Object) this;
    }
}
