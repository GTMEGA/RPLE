/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.helper;

import codechicken.multipart.TileMultipart;
import codechicken.multipart.minecraft.IPartMeta;
import codechicken.multipart.minecraft.McBlockPart;
import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.block.RPLEBlockBrightnessColorProvider;
import com.falsepattern.rple.api.common.color.CustomColor;
import com.falsepattern.rple.api.common.color.LightValueColor;
import com.falsepattern.rple.api.common.color.RPLEColor;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import static com.falsepattern.rple.api.common.RPLEColorUtil.COLOR_MIN;
import static com.falsepattern.rple.api.common.RPLEColorUtil.clampColorComponent;

@UtilityClass
public final class MultiPartHelper {
    @SuppressWarnings({"InstanceofIncompatibleInterface", "CastToIncompatibleInterface"})
    public static RPLEColor getMultiPartBrightnessColor(TileMultipart multiPart) {
        var multiPartRed = COLOR_MIN;
        var multiPartGreen = COLOR_MIN;
        var multiPartBlue = COLOR_MIN;

        for (val part : multiPart.jPartList()) {
            final RPLEColor partColor;
            if (part instanceof RPLEBlockBrightnessColorProvider) {
                val colorProvider = (RPLEBlockBrightnessColorProvider) part;
                partColor = colorProvider.rple$getCustomBrightnessColor();
            } else if (part instanceof McBlockPart) {
                val partBlock = (McBlockPart) part;
                val blockBase = partBlock.getBlock();
                val rpleBlock = (RPLEBlock) blockBase;

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
            multiPartRed = Math.max(multiPartRed, partColor.red());
            multiPartGreen = Math.max(multiPartGreen, partColor.green());
            multiPartBlue = Math.max(multiPartBlue, partColor.blue());
        }

        return new CustomColor(multiPartRed, multiPartGreen, multiPartBlue);
    }

    /**
     * As it stands, all Multi Parts are fully translucent.
     */
    public static RPLEColor getMultiPartTranslucencyColor(TileMultipart multiPart) {
        return LightValueColor.LIGHT_VALUE_15;
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
