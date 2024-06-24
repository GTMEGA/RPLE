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

package com.falsepattern.rple.internal.proxy;

import com.falsepattern.rple.internal.Tags;
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
        }
    }

    public void init(FMLInitializationEvent evt) {
        ColorConfigLoader.generateReadmeFile();
        blockColorManager().registerBlockColors();
    }

    public void postInit(FMLPostInitializationEvent evt) {
        if (RPLEConfig.General.ENABLE_LAMPS) {
            for (val lampData : Lamps.values()) {
                val name = lampData.name().toLowerCase();
                val lamp = GameRegistry.findBlock(Tags.MOD_ID, "lamp." + name);
                val dye = new ItemStack(Items.dye, 1, lampData.ordinal());
                GameRegistry.addShapedRecipe(new ItemStack(lamp, 8, 0), "LLL", "LDL", "LLL", 'L', Blocks.redstone_lamp, 'D', dye);
                GameRegistry.addShapelessRecipe(new ItemStack(lamp, 1, LampBlock.INVERTED_BIT), new ItemStack(lamp, 1, 0));
                GameRegistry.addShapelessRecipe(new ItemStack(lamp, 1, 0), new ItemStack(lamp, 1, LampBlock.INVERTED_BIT));
            }
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
