/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal;


import com.falsepattern.chunk.api.ChunkDataRegistry;
import com.falsepattern.falsetweaks.api.triangulator.VertexAPI;
import com.falsepattern.lumina.api.LumiWorldProviderRegistry;
import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.internal.storage.ColoredDataManager;
import com.falsepattern.rple.internal.storage.ColoredWorldProvider;
import lombok.Getter;
import lombok.val;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Tags.MODID,
     version = Tags.VERSION,
     name = Tags.MODNAME,
     acceptedMinecraftVersions = "[1.7.10]",
     dependencies = "required-after:falsepatternlib@[0.11,);required-after:lumina;required-after:falsetweaks@[2.3,)")
public class RPLE {
    private static final int[] COLOR_CHANNELS = new int[]{LightConstants.COLOR_CHANNEL_RED,
                                                          LightConstants.COLOR_CHANNEL_GREEN,
                                                          LightConstants.COLOR_CHANNEL_BLUE};
    @Getter
    private static int redIndexNoShader = 7; //Overrides vanilla value
    //These two grabbed from ftweaks
    @Getter
    private static int greenIndexNoShader;
    @Getter
    private static int blueIndexNoShader;

    @Getter
    private static int redIndexShader = 6; //Overrides optifine value
    //These two grabbed from ftweaks
    @Getter
    private static int greenIndexShader;
    @Getter
    private static int blueIndexShader;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LumiWorldProviderRegistry.hijack();
        int[] noShaderBuf = new int[2];
        int[] shaderBuf = new int[2];
        VertexAPI.allocateExtraVertexSlots(2, noShaderBuf, shaderBuf);
        greenIndexNoShader = noShaderBuf[0];
        blueIndexNoShader = noShaderBuf[1];
        greenIndexShader = shaderBuf[0];
        blueIndexShader = shaderBuf[1];
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