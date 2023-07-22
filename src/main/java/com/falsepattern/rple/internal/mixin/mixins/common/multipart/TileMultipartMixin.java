/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.multipart;

import codechicken.lib.world.IChunkLoadTile;
import codechicken.multipart.TileMultipart;
import com.falsepattern.rple.api.common.block.RPLEBlockBrightnessColorProvider;
import com.falsepattern.rple.api.common.block.RPLEBlockTranslucencyColorProvider;
import com.falsepattern.rple.api.common.color.RPLEColor;
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
