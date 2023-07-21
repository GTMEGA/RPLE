package com.falsepattern.rple.internal.proxy;

import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.common.block.LampBlock;
import com.falsepattern.rple.internal.common.block.LampItemBlock;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;
import lombok.NoArgsConstructor;
import lombok.val;

import static com.falsepattern.rple.internal.common.block.BlockColorManager.blockColorManager;
import static com.falsepattern.rple.internal.common.event.LumiEventHandler.lumiEventHandler;

@NoArgsConstructor
public abstract class CommonProxy {
    public void preInit(FMLPreInitializationEvent evt) {
        lumiEventHandler().registerEventHandler();
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

    public void init(FMLInitializationEvent evt) {
        blockColorManager().registerBlockColors();
    }

    public void postInit(FMLPostInitializationEvent evt) {
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
