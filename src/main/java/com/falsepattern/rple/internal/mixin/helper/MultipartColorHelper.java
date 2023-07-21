/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.helper;

import codechicken.multipart.TileMultipart;
import com.falsepattern.rple.api.RPLEColorAPI;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

//TODO: [PRE-RELEASE] Fix this compatibility patch
public class MultipartColorHelper {
    public static int getColoredLightValue(Block vBlock, IBlockAccess world, int meta, int colorChannel, int x, int y, int z) {
        val tileEntity = world.getTileEntity(x, y, z);

        if (!(tileEntity instanceof TileMultipart))
            return -1;

        val tileMultipart = (TileMultipart) tileEntity;
        val iter = tileMultipart.partList().iterator();
        int sum = 0;
//        while (iter.hasNext()) {
//            val element = iter.next();
//            if (element instanceof ILight) {
//                val lightElement = (ILight) element;
//                if (lightElement.isOn()) {
//                    sum += LightConstants.colors[colorChannel][~lightElement.getColor() & 15];
//                }
//            } else if (element instanceof RedstoneTorchPart) {
//                val torch = ((RedstoneTorchPart) element);
//                if (torch.active()) {
//                    sum += ((OldColoredBlockInternal) torch.getBlock()).getColoredLightValueRaw(0, colorChannel);
//                }
//            } else if (element instanceof McBlockPart) {
//                val block = (OldColoredBlockInternal) ((McBlockPart) element).getBlock();
//                sum += block.getColoredLightValueRaw(0, colorChannel);
//            }
//        }

        return RPLEColorAPI.clampColorComponent(sum);
    }
}
