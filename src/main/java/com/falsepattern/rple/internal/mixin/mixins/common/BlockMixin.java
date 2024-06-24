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

package com.falsepattern.rple.internal.mixin.mixins.common;

import com.falsepattern.rple.api.common.block.RPLEBlockRenamed;
import com.falsepattern.rple.internal.mixin.hook.ColoredLightingHooks;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
@SuppressWarnings("unused")
public abstract class BlockMixin implements RPLEBlockRenamed {
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
