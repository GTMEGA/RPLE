/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.mrtjpcore;

import mrtjp.core.block.InstancedBlock;
import org.spongepowered.asm.mixin.Mixin;

import java.lang.invoke.MethodHandles;

@Mixin(InstancedBlock.class)
public abstract class InstancedBlockMixin {
    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    //TODO: [PRE-RELEASE] Fix this compatibility patch
//    @Override
//    public int getColoredLightValue(IBlockAccess world, int meta, int colorChannel, int x, int y, int z) {
//        TileEntity te = world.getTileEntity(x, y, z);
//        int color;
//
//        if (te instanceof InstancedBlockTile && te instanceof OldColoredBlock) {
//            OldColoredBlock colorTE = (OldColoredBlock) te;
//            color = colorTE.getColoredLightValue(world, meta, colorChannel, x, y, z);
//        } else if (te instanceof InstancedBlockTile) {
//            InstancedBlockTile tile = (InstancedBlockTile) te;
//            color = tile.getLightValue();
//        } else {
//            color = SuperCallHelper.getColoredLightValue(lookup, InstancedBlock.class, this, world, meta, colorChannel, x, y, z);
//        }
//        return color;
//    }

}
