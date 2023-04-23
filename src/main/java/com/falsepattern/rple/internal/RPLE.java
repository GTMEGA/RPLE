/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal;


import com.falsepattern.chunk.api.ChunkDataRegistry;
import com.falsepattern.lumina.api.LumiWorldProviderRegistry;
import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.internal.storage.ColoredDataManager;
import com.falsepattern.rple.internal.storage.ColoredWorldProvider;
import lombok.val;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Tags.MODID,
     version = Tags.VERSION,
     name = Tags.MODNAME,
     acceptedMinecraftVersions = "[1.7.10]",
     dependencies = "required-after:falsepatternlib@[0.11,);required-after:lumina")
public class RPLE {
    private static final int[] COLOR_CHANNELS = new int[]{LightConstants.COLOR_CHANNEL_RED,
                                                          LightConstants.COLOR_CHANNEL_GREEN,
                                                          LightConstants.COLOR_CHANNEL_BLUE};

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LumiWorldProviderRegistry.hijack();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        for (val colorChannel: COLOR_CHANNELS) {
            ChunkDataRegistry.registerDataManager(new ColoredDataManager(colorChannel, colorChannel == LightConstants.COLOR_CHANNEL_RED));
            LumiWorldProviderRegistry.registerWorldProvider(new ColoredWorldProvider(colorChannel));
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ChunkDataRegistry.disableDataManager("minecraft", "blocklight");
        ChunkDataRegistry.disableDataManager("minecraft", "skylight");
    }
}