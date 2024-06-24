/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
 */

package com.falsepattern.rple.internal.mixin.helper;

import codechicken.multipart.TileMultipart;
import codechicken.multipart.minecraft.IPartMeta;
import codechicken.multipart.minecraft.McBlockPart;
import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.block.RPLECustomBlockBrightness;
import com.falsepattern.rple.api.common.color.LightValueColor;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import static com.falsepattern.rple.api.common.ServerColorHelper.*;

@UtilityClass
public final class MultiPartHelper {
    @SuppressWarnings({"InstanceofIncompatibleInterface", "CastToIncompatibleInterface"})
    public static short getMultiPartBrightnessColor(TileMultipart multiPart) {
        var multiPartRed = COLOR_MIN;
        var multiPartGreen = COLOR_MIN;
        var multiPartBlue = COLOR_MIN;

        for (val part : multiPart.jPartList()) {
            final short partColor;
            if (part instanceof RPLECustomBlockBrightness) {
                val colorProvider = (RPLECustomBlockBrightness) part;
                partColor = colorProvider.rple$getCustomBrightnessColor();
            } else if (part instanceof McBlockPart) {
                val partBlock = (McBlockPart) part;
                val blockBase = partBlock.getBlock();
                val rpleBlock = RPLEBlock.of(blockBase);

                if (part instanceof IPartMeta) {
                    val partMeta = (IPartMeta) part;
                    val blockMeta = partMeta.getMetadata();
                    partColor = rpleBlock.rple$getBrightnessColor(blockMeta);
                } else {
                    partColor = rpleBlock.rple$getBrightnessColor();
                }
            } else {
                continue;
            }
            multiPartRed = Math.max(multiPartRed, red(partColor));
            multiPartGreen = Math.max(multiPartGreen, green(partColor));
            multiPartBlue = Math.max(multiPartBlue, blue(partColor));
        }

        return RGB16FromRGBChannel4Bit(multiPartRed, multiPartGreen, multiPartBlue);
    }

    /**
     * As it stands, all Multi Parts are fully translucent.
     */
    public static short getMultiPartTranslucencyColor(TileMultipart multiPart) {
        return LightValueColor.LIGHT_VALUE_15.rgb16();
    }

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

        return clampColorComponent(sum);
    }
}
