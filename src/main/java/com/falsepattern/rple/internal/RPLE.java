/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal;


import com.falsepattern.chunk.api.ChunkDataRegistry;
import com.falsepattern.lumina.api.LumiWorldProviderRegistry;
import com.falsepattern.rple.internal.storage.ColoredDataManager;
import com.falsepattern.rple.internal.storage.ColoredLightChannel;
import com.falsepattern.rple.internal.storage.ColoredWorldProvider;
import lombok.val;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = Tags.MODID,
     version = Tags.VERSION,
     name = Tags.MODNAME,
     acceptedMinecraftVersions = "[1.7.10]",
     dependencies = "required-after:falsepatternlib@[0.11,);required-after:lumina")
public class RPLE {
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        for (val channel: ColoredLightChannel.values()) {
            ChunkDataRegistry.registerDataManager(new ColoredDataManager(channel));
            LumiWorldProviderRegistry.registerWorldProvider(new ColoredWorldProvider(ColoredLightChannel.RED));
            LumiWorldProviderRegistry.registerWorldProvider(new ColoredWorldProvider(ColoredLightChannel.GREEN));
            LumiWorldProviderRegistry.registerWorldProvider(new ColoredWorldProvider(ColoredLightChannel.BLUE));
        }
    }
}