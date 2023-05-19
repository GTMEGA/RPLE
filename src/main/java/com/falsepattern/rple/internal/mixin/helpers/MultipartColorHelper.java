/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.helpers;

import codechicken.multipart.BlockMultipart;
import codechicken.multipart.TileMultipart;
import codechicken.multipart.minecraft.McBlockPart;
import codechicken.multipart.minecraft.RedstoneTorchPart;
import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.internal.mixin.interfaces.ColoredBlockInternal;
import lombok.val;
import mrtjp.projectred.illumination.BaseLightPart;
import mrtjp.projectred.illumination.ILight;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

public class MultipartColorHelper {
    public static int getColoredLightValue(Block vBlock, IBlockAccess world, int meta, int colorChannel, int x, int y, int z) {
        val tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileMultipart) {
            val tileMultipart = (TileMultipart) tile;
            val iter = tileMultipart.partList().iterator();
            int sum = 0;
            while (iter.hasNext()) {
                val element = iter.next();
                if (element instanceof ILight) {
                    val lightElement = (ILight) element;
                    if (lightElement.isOn()) {
                        sum += LightConstants.colors[colorChannel][~lightElement.getColor() & 15];
                    }
                } else if (element instanceof RedstoneTorchPart) {
                    val torch = ((RedstoneTorchPart)element);
                    if (torch.active()) {
                        sum += ((ColoredBlockInternal)torch.getBlock()).getColoredLightValueRaw(0, colorChannel);
                    }
                } else if (element instanceof McBlockPart) {
                    val block = (ColoredBlockInternal)((McBlockPart) element).getBlock();
                    sum += block.getColoredLightValueRaw(0, colorChannel);
                }
            }
            return Math.min(sum, 15);
        }
        return -1;
    }
}
