/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal;


import com.falsepattern.falsetweaks.api.triangulator.VertexAPI;
import com.falsepattern.rple.internal.client.render.LampRenderer;
import com.falsepattern.rple.internal.common.block.LampBlock;
import com.falsepattern.rple.internal.common.block.LampItemBlock;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import lombok.Getter;
import lombok.val;

import static com.falsepattern.rple.internal.client.lightmap.LightMapPipeline.lightMapPipeline;
import static com.falsepattern.rple.internal.common.block.BlockColorLoader.blockColorLoader;
import static com.falsepattern.rple.internal.event.LumiEventHandler.lumiEventHandler;

@Mod(modid = Tags.MOD_ID,
     version = Tags.VERSION,
     name = Tags.MOD_NAME,
     acceptedMinecraftVersions = "[1.7.10]",
     dependencies = "required-after:falsepatternlib@[0.11,);" +
                    "required-after:chunkapi@[0.2,);" +
                    "required-after:lumina;" +
                    "required-after:falsetweaks@[2.4,)")
public class RPLE {
    @Getter
    private static int redIndexNoShader = 7; //Overrides vanilla value
    //These two grabbed from ftweaks
    @Getter
    private static int greenIndexNoShader;
    @Getter
    private static int blueIndexNoShader;
    @Getter
    private static int rpleEdgeTexUIndexNoShader;
    @Getter
    private static int rpleEdgeTexVIndexNoShader;

    @Getter
    private static int redIndexShader = 6; //Overrides optifine value
    //These two grabbed from ftweaks
    @Getter
    private static int greenIndexShader;
    @Getter
    private static int blueIndexShader;
    @Getter
    private static int rpleEdgeTexUIndexShader;
    @Getter
    private static int rpleEdgeTexVIndexShader;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        lumiEventHandler().registerEventHandler();

        val noShaderBuf = new int[4];
        val shaderBuf = new int[4];
        VertexAPI.allocateExtraVertexSlots(4, noShaderBuf, shaderBuf);
        greenIndexNoShader = noShaderBuf[0];
        blueIndexNoShader = noShaderBuf[1];
        rpleEdgeTexUIndexNoShader = noShaderBuf[2];
        rpleEdgeTexVIndexNoShader = noShaderBuf[3];
        greenIndexShader = shaderBuf[0];
        blueIndexShader = shaderBuf[1];
        rpleEdgeTexUIndexShader = shaderBuf[2];
        rpleEdgeTexVIndexShader = shaderBuf[3];

        val lamp = new LampBlock();
        lamp.setBlockName(Tags.MOD_ID + ".lamp." + "blue").setBlockTextureName("blue");
        GameRegistry.registerBlock(lamp, LampItemBlock.class, "lamp." + "blue");

        //TODO: [PRE-RELEASE] Fix these values
//        if (RPLEConfig.ENABLE_LAMPS) {
//            for (val lampData : Lamps.values()) {
//                val name = lampData.name().toLowerCase();
//                val r = lampData.r;
//                val g = lampData.g;
//                val b = lampData.b;
//                val lamp = new LampBlock();
//                lamp.setBlockName(Tags.MODID + ".lamp." + name).setBlockTextureName(name);
//                lamp.setColoredLightValue(0, 0, 0, 0);
//                lamp.setColoredLightValue(LampBlock.INVERTED_BIT, r, g, b);
//                lamp.setColoredLightValue(LampBlock.POWERED_BIT, r, g, b);
//                lamp.setColoredLightValue(LampBlock.INVERTED_BIT | LampBlock.POWERED_BIT, 0, 0, 0);
//                GameRegistry.registerBlock(lamp, LampItemBlock.class, "lamp." + name);
//            }
//        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        RenderingRegistry.registerBlockHandler(new LampRenderer());
        lightMapPipeline().registerLightMaps();
        blockColorLoader().registerBlockColors();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }
}
