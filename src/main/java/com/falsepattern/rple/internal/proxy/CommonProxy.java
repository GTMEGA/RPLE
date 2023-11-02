/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.proxy;

import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.api.common.lamp.LampBlock;
import com.falsepattern.rple.api.common.lamp.LampItemBlock;
import com.falsepattern.rple.internal.common.block.Lamps;
import com.falsepattern.rple.internal.common.block.RPLELampBlock;
import com.falsepattern.rple.internal.common.config.ColorConfigLoader;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;

import com.falsepattern.rple.internal.common.config.RPLEConfig;
import lombok.NoArgsConstructor;
import lombok.val;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static com.falsepattern.rple.internal.common.block.BlockColorManager.blockColorManager;
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
        if (RPLEConfig.ENABLE_LAMPS) {
            for (val lampData : Lamps.values()) {
                val name = lampData.name().toLowerCase();
                val lamp = GameRegistry.findBlock(Tags.MOD_ID, "lamp." + name);
                val dye = new ItemStack(Items.dye, 1, lampData.ordinal());
                val lamp_regular = new ItemStack(lamp, 1, 0);
                val lamp_inverted = new ItemStack(lamp, 1, LampBlock.INVERTED_BIT);
                GameRegistry.addShapedRecipe(lamp_regular, "LLL", "LDL", "LLL", 'L', Blocks.redstone_lamp, 'D', dye);
                GameRegistry.addShapelessRecipe(lamp_inverted, lamp_regular);
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
