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
import com.falsepattern.lib.util.ResourceUtil;
import com.falsepattern.lumina.api.LumiWorldProviderRegistry;
import com.falsepattern.rple.api.ColoredBlock;
import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.api.lightmap.LightMapBase;
import com.falsepattern.rple.api.lightmap.LightMapMask;
import com.falsepattern.rple.api.lightmap.LightMapMaskType;
import com.falsepattern.rple.api.lightmap.LightMapPipelineRegistry;
import com.falsepattern.rple.internal.blocks.Lamp;
import com.falsepattern.rple.internal.client.render.LampRenderingHandler;
import com.falsepattern.rple.internal.lightmap.builtin.base.BossColorModifier;
import com.falsepattern.rple.internal.lightmap.builtin.base.NightVisionMask;
import com.falsepattern.rple.internal.lightmap.builtin.base.VanillaLightMapBase;
import com.falsepattern.rple.internal.storage.ColoredDataManager;
import com.falsepattern.rple.internal.storage.ColoredWorldProvider;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

import lombok.*;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

@Mod(modid = Tags.MODID,
     version = Tags.VERSION,
     name = Tags.MODNAME,
     acceptedMinecraftVersions = "[1.7.10]",
     dependencies = "required-after:falsepatternlib@[0.11,);" +
                    "required-after:chunkapi@[0.2,);" +
                    "required-after:lumina;" +
                    "required-after:falsetweaks@[2.4,)")
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
        val blueLamp = (Lamp) new Lamp(false).setBlockName("lamp_blue").setBlockTextureName(Tags.MODID + ":lamp_blue_off");
        val blueLampPowered = (Lamp) new Lamp(true).setBlockName("lamp_blue_on").setBlockTextureName(Tags.MODID + ":lamp_blue_on");
        blueLampPowered.setColoredLightValue(0, 0, 0, 15);
        blueLamp.setOpposite(blueLampPowered);
        blueLampPowered.setOpposite(blueLamp);
        GameRegistry.registerBlock(blueLamp, "lamp_blue");
        GameRegistry.registerBlock(blueLampPowered, "lamp_blue_on");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        for (val colorChannel: COLOR_CHANNELS) {
            ChunkDataRegistry.registerDataManager(new ColoredDataManager(colorChannel, colorChannel == LightConstants.COLOR_CHANNEL_RED));
            LumiWorldProviderRegistry.registerWorldProvider(new ColoredWorldProvider(colorChannel));
        }
        RenderingRegistry.registerBlockHandler(new LampRenderingHandler());
    }

    private static LightMapBase lightMapBase = new VanillaLightMapBase();
    private static LightMapMask nightVisionMask = new NightVisionMask();
    private static LightMapMask bossColorMask = new BossColorModifier();

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ChunkDataRegistry.disableDataManager("minecraft", "blocklight");
        ChunkDataRegistry.disableDataManager("minecraft", "skylight");
        try {
            setupLightValues();
        } catch (IOException e) {
            Common.LOG.error("Could not set up light values", e);
        }
        val lightMapRegistry = LightMapPipelineRegistry.getInstance();
        lightMapRegistry.registerBase(lightMapBase, 1000);
        lightMapRegistry.registerMask(nightVisionMask, LightMapMaskType.BLOCK);
        lightMapRegistry.registerMask(nightVisionMask, LightMapMaskType.SKY);
        lightMapRegistry.registerMask(bossColorMask, LightMapMaskType.BLOCK);
        lightMapRegistry.registerMask(bossColorMask, LightMapMaskType.SKY);
    }

    private static final Map<String, RGB> valueCache = new HashMap<>();
    private static int currentLine = 0;

    private static void setupLightValues() throws IOException {
        val data = ResourceUtil.getResourceStringFromJar("/default_lightvals.txt", RPLE.class);
        val lines = new StringTokenizer(data, "\r\n");
        while (lines.hasMoreTokens()) {
            currentLine++;
            val unSanitized = lines.nextToken();
            //Remove comments
            val sanitized = new StringTokenizer(unSanitized, "#").nextToken().trim();
            if (sanitized.length() == 0) {
                continue;
            }
            val splitter = new StringTokenizer(sanitized, "=");
            if (splitter.countTokens() != 2) {
                Common.LOG.error("Config line {} is malformed (multiple equal signs): {}", currentLine, unSanitized);
                continue;
            }
            val assignee = splitter.nextToken().trim();
            val value = resolve(splitter.nextToken().trim());
            val targetSplitter = new StringTokenizer(assignee, "/");
            val count = targetSplitter.countTokens();
            if (count < 1 || count > 2) {
                Common.LOG.error("Config line {} is malformed (invalid assignee): {}", currentLine, assignee);
                continue;
            }
            val id = targetSplitter.nextToken();
            int meta = 0;
            if (count == 2) {
                val metaToken = targetSplitter.nextToken();
                try {
                    meta = Integer.parseInt(metaToken);
                } catch (NumberFormatException e) {
                    Common.LOG.error("Config line {} malformed (invalid metadata): {}", currentLine, metaToken);
                }
            }
            val block = GameData.getBlockRegistry().get(id);
            if (block == null || block == Blocks.air) {
                Common.LOG.error("Config line {} malformed (unknown block id): {}", currentLine, id);
                continue;
            }
            val cBlock = ((ColoredBlock)block);
            cBlock.setColoredLightValue(meta, value.r, value.g, value.b);
            valueCache.put(assignee, value);
        }
    }

    private static final RGB EMPTY = new RGB(0, 0, 0);

    private static RGB resolve(String value) {
        if (value.charAt(0) == '@') {
            val key = value.substring(1);
            if (!valueCache.containsKey(key)) {
                Common.LOG.error("Config line {} malformed (nonexistent backref): {}", currentLine, key);
                return EMPTY;
            }
            return valueCache.get(key);
        }
        if (value.length() != 3) {
            Common.LOG.error("Config line {} malformed (invalid light value): {}", currentLine, value);
            return EMPTY;
        }
        final int packed;
        try {
            packed = Integer.parseInt(value, 16);
        } catch (NumberFormatException e) {
            Common.LOG.error("Config line {} malformed (unparseable hex number): {}", currentLine, value);
            return EMPTY;
        }
        return new RGB((packed & 0xF00) >>> 8, (packed & 0x0F0) >>> 4, (packed & 0x00F));
    }

    @Data
    private static class RGB {
        private final int r;
        private final int g;
        private final int b;
    }
}