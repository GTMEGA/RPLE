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

package com.falsepattern.rple.internal.proxy;

import com.falsepattern.rple.internal.common.config.ColorConfigLoader;
import com.falsepattern.rple.internal.common.config.RPLEConfig;
import com.falsepattern.rple.internal.common.lamp.LampBlock;
import com.falsepattern.rple.internal.common.lamp.LampItemBlock;
import com.falsepattern.rple.internal.common.lamp.Lamps;
import com.falsepattern.rple.internal.common.lamp.RPLELampBlock;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;
import lombok.NoArgsConstructor;
import lombok.val;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;

import static com.falsepattern.rple.internal.common.colorizer.BlockColorManager.blockColorManager;
import static com.falsepattern.rple.internal.common.event.LumiEventHandler.lumiEventHandler;

@NoArgsConstructor
public abstract class CommonProxy {
    public void preInit(FMLPreInitializationEvent evt) {
        lumiEventHandler().registerEventHandler();

        for (val lampData: Lamps.values()) {
            val name = lampData.name().toLowerCase();
            val lamp = new RPLELampBlock(lampData);
            GameRegistry.registerBlock(lamp, LampItemBlock.class, "lamp." + name);
            OreDictionary.registerOre("lampColored", lamp);
        }
    }

    public void init(FMLInitializationEvent evt) {
        ColorConfigLoader.generateReadmeFile();
        blockColorManager().registerBlockColors();
    }

    public void postInit(FMLPostInitializationEvent evt) {
        if (RPLEConfig.General.CRAFTABLE_LAMPS) {
            for (val lampData : Lamps.values()) {
                val lamp = lampData.findBlock();
                val dyes = lampData.dyes;
                val recipeArgs = new ArrayList<Object>();

                for (val dye: dyes) {
                    recipeArgs.add(Blocks.redstone_lamp);
                    recipeArgs.add(dye);
                }
                GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(lamp, dyes.length, 0), recipeArgs.toArray()));
                GameRegistry.addShapelessRecipe(new ItemStack(lamp, 1, LampBlock.INVERTED_BIT), new ItemStack(lamp, 1, 0));
                GameRegistry.addShapelessRecipe(new ItemStack(lamp, 1, 0), new ItemStack(lamp, 1, LampBlock.INVERTED_BIT));
            }
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.redstone_lamp, 1, 0), "lampColored", Items.water_bucket));
        }
    }

    public void serverAboutToStart(FMLServerAboutToStartEvent evt) {
    }

    public void serverStarting(FMLServerStartingEvent evt) {
    }

    public void serverStarted(FMLServerStartedEvent evt) {
    }

    public void serverStopping(FMLServerStoppingEvent evt) {
    }

    public void serverStopped(FMLServerStoppedEvent evt) {
    }
}
