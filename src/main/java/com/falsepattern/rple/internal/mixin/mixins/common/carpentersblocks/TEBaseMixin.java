/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
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

package com.falsepattern.rple.internal.mixin.mixins.common.carpentersblocks;

import com.carpentersblocks.tileentity.TEBase;
import com.carpentersblocks.util.Attribute;
import com.carpentersblocks.util.BlockProperties;
import com.carpentersblocks.util.registry.FeatureRegistry;
import com.falsepattern.rple.api.common.ServerColorHelper;
import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.block.RPLECustomBlockBrightness;
import com.falsepattern.rple.api.common.color.LightValueColor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import java.util.Map;

@Mixin(TEBase.class)
public abstract class TEBaseMixin extends TileEntity implements RPLECustomBlockBrightness {
    @Shadow(remap = false) public abstract boolean hasAttribute(byte attrId);

    @Shadow(remap = false) protected Map<Byte, Attribute> cbAttrMap;

    @Shadow(remap = false) public abstract void setMetadata(int metadata);

    @Shadow(remap = false) public abstract void restoreMetadata();

    @Unique
    private short rple$lightValue;
    @Override
    public short rple$getCustomBrightnessColor() {
        return rple$lightValue;
    }

    @Override
    public short rple$getCustomBrightnessColor(int blockMeta) {
        return rple$lightValue;
    }

    @Override
    public short rple$getCustomBrightnessColor(@NotNull IBlockAccess world,
                                               int blockMeta,
                                               int posX,
                                               int posY,
                                               int posZ) {
        return rple$lightValue;
    }

    @Inject(method = "updateCachedLighting",
            remap = false,
            at = @At("HEAD"))
    private void updateCachedLighting(CallbackInfo ci) {
        rple$lightValue = rple$getDynamicLightValue();
    }

    @Unique
    protected short rple$getDynamicLightValue() {
        short value = 0;
        if (FeatureRegistry.enableIllumination && this.hasAttribute((byte)21)) {
            return LightValueColor.LIGHT_VALUE_15.rgb16();
        } else {
            val world = getWorldObj();
            for(Map.Entry pair : this.cbAttrMap.entrySet()) {
                ItemStack itemStack = BlockProperties.getCallableItemStack(((Attribute)pair.getValue()).getItemStack());
                Block block = BlockProperties.toBlock(itemStack);
                if (block != Blocks.air) {
                    val meta = itemStack.getItemDamage();
                    this.setMetadata(meta);
                    short sensitiveLight = ((RPLEBlock)block).rple$getBrightnessColor(world, meta, this.xCoord, this.yCoord, this.zCoord);
                    this.restoreMetadata();
                    value = ServerColorHelper.max(value, sensitiveLight);
                }
            }
            return value;
        }
    }
}
