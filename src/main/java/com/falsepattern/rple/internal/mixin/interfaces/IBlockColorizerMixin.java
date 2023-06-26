/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.interfaces;

import com.falsepattern.rple.api.color.RPLEColor;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.Nullable;

public interface IBlockColorizerMixin {
    void baseColoredBrightness(@Nullable RPLEColor baseColouredBrightness);

    void baseColoredTranslucency(@Nullable RPLEColor baseColoredTranslucency);

    void metaColoredBrightness(@Nullable RPLEColor @Nullable [] metaColoredBrightness);

    void metaColoredTranslucency(@Nullable RPLEColor @Nullable [] metaColoredTranslucency);

    //TODO: can this be made to work? would make working with the rest of the code base less tedious imo
//    @Override
//    default RPLEColor getColoredBrightness(IBlockAccess world, int blockMeta, int posX, int posY, int posZ) {
//        return null;
//    }
//
//    @Override
//    default RPLEColor getColoredBrightness() {
//        return null;
//    }
//
//    @Override
//    default RPLEColor getColoredTranslucency(IBlockAccess world, int blockMeta, int posX, int posY, int posZ) {
//        return null;
//    }
//
//    @Override
//    default RPLEColor getColoredTranslucency() {
//        return null;
//    }
    default RPLEColor rple$getColoredBrightness() {
        return rple$getColoredBrightness(0);
    }

    RPLEColor rple$getColoredBrightness(int blockMeta);

    RPLEColor rple$getColoredBrightness(IBlockAccess world, int blockMeta, int posX, int posY, int posZ);

    default RPLEColor rple$getColoredTranslucency() {
        return rple$getColoredTranslucency(0);
    }

    RPLEColor rple$getColoredTranslucency(int blockMeta);

    RPLEColor rple$getColoredTranslucency(IBlockAccess world, int blockMeta, int posX, int posY, int posZ);
}
