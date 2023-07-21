/*
 * Copyright (c) 2023 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.multipart;

import codechicken.lib.world.IChunkLoadTile;
import codechicken.multipart.TileMultipart;
import com.falsepattern.rple.api.block.RPLEBlockBrightnessColorProvider;
import com.falsepattern.rple.api.block.RPLEBlockTranslucencyColorProvider;
import com.falsepattern.rple.api.color.RPLEColor;
import com.falsepattern.rple.internal.mixin.helper.MultiPartHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TileMultipart.class)
public abstract class TileMultipartMixin extends TileEntity implements IChunkLoadTile,
                                                                       RPLEBlockBrightnessColorProvider,
                                                                       RPLEBlockTranslucencyColorProvider {
    @Override
    public @NotNull RPLEColor rple$getCustomBrightnessColor() {
        return MultiPartHelper.getMultiPartBrightnessColor(thiz());
    }

    @Override
    public @NotNull RPLEColor rple$getCustomBrightnessColor(int blockMeta) {
        return MultiPartHelper.getMultiPartBrightnessColor(thiz());
    }

    @Override
    public @NotNull RPLEColor rple$getCustomBrightnessColor(@NotNull IBlockAccess world,
                                                            int blockMeta,
                                                            int posX,
                                                            int posY,
                                                            int posZ) {
        return MultiPartHelper.getMultiPartBrightnessColor(thiz());
    }

    @Override
    public @NotNull RPLEColor rple$getCustomTranslucencyColor() {
        return MultiPartHelper.getMultiPartTranslucencyColor(thiz());
    }

    @Override
    public @NotNull RPLEColor rple$getCustomTranslucencyColor(int blockMeta) {
        return MultiPartHelper.getMultiPartTranslucencyColor(thiz());
    }

    @Override
    public @NotNull RPLEColor rple$getCustomTranslucencyColor(@NotNull IBlockAccess world,
                                                              int blockMeta,
                                                              int posX,
                                                              int posY,
                                                              int posZ) {
        return MultiPartHelper.getMultiPartTranslucencyColor(thiz());
    }

    private TileMultipart thiz() {
        return (TileMultipart) (Object) this;
    }
}
