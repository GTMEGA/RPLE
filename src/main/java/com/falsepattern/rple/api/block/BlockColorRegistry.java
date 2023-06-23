/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.block;

import com.falsepattern.rple.api.color.RPLEColour;
import net.minecraft.block.Block;

public interface BlockColorRegistry {
    void addPaletteColor(String name, RPLEColour color);

    void addPaletteColor(String name, String color);

    void setBlockBrightness(Block block, int blockMeta, RPLEColour color);

    void setBlockBrightness(Block block, int blockMeta, String color);

    void setBlockBrightness(Block block, RPLEColour color);

    void setBlockBrightness(Block block, String color);

    void setBlockBrightness(String blockName, RPLEColour color);

    void setBlockBrightness(String blockName, String color);

    void setBlockTranslucency(Block block, int blockMeta, RPLEColour color);

    void setBlockTranslucency(Block block, int blockMeta, String color);

    void setBlockTranslucency(Block block, RPLEColour color);

    void setBlockTranslucency(Block block, String color);

    void setBlockTranslucency(String blockName, RPLEColour color);

    void setBlockTranslucency(String blockName, String color);
}