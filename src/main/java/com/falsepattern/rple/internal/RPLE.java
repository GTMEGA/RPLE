/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal;


import com.falsepattern.chunk.api.ChunkDataRegistry;
import com.falsepattern.falsetweaks.api.triangulator.VertexAPI;
import com.falsepattern.lib.util.ResourceUtil;
import com.falsepattern.lumina.api.LumiWorldProviderRegistry;
import com.falsepattern.rple.Lamps;
import com.falsepattern.rple.api.ColoredBlock;
import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.api.RPLELightMapAPI;
import com.falsepattern.rple.api.lightmap.LightMapBase;
import com.falsepattern.rple.api.lightmap.LightMapMask;
import com.falsepattern.rple.api.lightmap.vanilla.BossColorModifierMask;
import com.falsepattern.rple.api.lightmap.vanilla.NightVisionMask;
import com.falsepattern.rple.api.lightmap.vanilla.VanillaLightMapBase;
import com.falsepattern.rple.internal.block.LampBlock;
import com.falsepattern.rple.internal.block.LampItemBlock;
import com.falsepattern.rple.internal.client.render.LampRenderer;
import com.falsepattern.rple.internal.common.storage.ColoredDataManager;
import com.falsepattern.rple.internal.common.storage.ColoredWorldProvider;
import com.falsepattern.rple.internal.config.RPLEConfig;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import lombok.Data;
import lombok.Getter;
import lombok.val;
import net.minecraft.init.Blocks;

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
    public static final String[] IDs = new String[]{"RED", "GREEN", "BLUE"};

    private static final int[] COLOR_CHANNELS = new int[]{LightConstants.COLOR_CHANNEL_RED,
            LightConstants.COLOR_CHANNEL_GREEN,
            LightConstants.COLOR_CHANNEL_BLUE};

    private static final LightMapBase VANILLA_LIGHT_MAP_BASE = new VanillaLightMapBase();
    private static final LightMapMask NIGHT_VISION_MASK = new NightVisionMask();
    private static final LightMapMask BOSS_COLOR_MODIFIER_MASK = new BossColorModifierMask();

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
    public void preInit(FMLPreInitializationEvent event) throws IOException {
        LumiWorldProviderRegistry.hijack();
        int[] noShaderBuf = new int[4];
        int[] shaderBuf = new int[4];
        VertexAPI.allocateExtraVertexSlots(4, noShaderBuf, shaderBuf);
        greenIndexNoShader = noShaderBuf[0];
        blueIndexNoShader = noShaderBuf[1];
        rpleEdgeTexUIndexNoShader = noShaderBuf[2];
        rpleEdgeTexVIndexNoShader = noShaderBuf[3];
        greenIndexShader = shaderBuf[0];
        blueIndexShader = shaderBuf[1];
        rpleEdgeTexUIndexShader = shaderBuf[2];
        rpleEdgeTexVIndexShader = shaderBuf[3];
        if (RPLEConfig.ENABLE_LAMPS) {
            for (val lampData : Lamps.values()) {
                val name = lampData.name().toLowerCase();
                val r = lampData.r;
                val g = lampData.g;
                val b = lampData.b;
                val lamp = new LampBlock();
                lamp.setBlockName(Tags.MODID + ".lamp." + name).setBlockTextureName(name);
                lamp.setColoredLightValue(0, 0, 0, 0);
                lamp.setColoredLightValue(LampBlock.INVERTED_BIT, r, g, b);
                lamp.setColoredLightValue(LampBlock.POWERED_BIT, r, g, b);
                lamp.setColoredLightValue(LampBlock.INVERTED_BIT | LampBlock.POWERED_BIT, 0, 0, 0);
                GameRegistry.registerBlock(lamp, LampItemBlock.class, "lamp." + name);
            }
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        for (val colorChannel : COLOR_CHANNELS) {
            ChunkDataRegistry.registerDataManager(new ColoredDataManager(colorChannel));
            LumiWorldProviderRegistry.registerWorldProvider(new ColoredWorldProvider(colorChannel));
        }
        RenderingRegistry.registerBlockHandler(new LampRenderer());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ChunkDataRegistry.disableDataManager("minecraft", "blocklight");
        ChunkDataRegistry.disableDataManager("minecraft", "skylight");
        try {
            setupLightValues();
        } catch (IOException e) {
            Common.LOG.error("Could not set up light values", e);
        }

        RPLELightMapAPI.registerLightMapBase(VANILLA_LIGHT_MAP_BASE, 1000);
        RPLELightMapAPI.registerLightMapMask(NIGHT_VISION_MASK);
        RPLELightMapAPI.registerLightMapMask(BOSS_COLOR_MODIFIER_MASK);
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
            val comment = unSanitized.indexOf('#');
            final String sanitized;
            if (comment >= 0) {
                sanitized = unSanitized.substring(0, comment).trim();
            } else {
                sanitized = unSanitized.trim();
            }
            if (sanitized.length() == 0) {
                continue;
            }
            val splitter = new StringTokenizer(sanitized, "=");
            if (splitter.countTokens() > 2) {
                Common.LOG.error("ColorConfig line {} is malformed (multiple equal signs): {}", currentLine, unSanitized);
                continue;
            }
            if (splitter.countTokens() < 2) {
                Common.LOG.error("ColorConfig line {} is malformed (missing equal sign): {}", currentLine, unSanitized);
                continue;
            }
            val assignee = splitter.nextToken().trim();
            val value = resolve(splitter.nextToken().trim());
            val targetSplitter = new StringTokenizer(assignee, "/");
            val count = targetSplitter.countTokens();
            if (count < 1 || count > 2) {
                Common.LOG.error("ColorConfig line {} is malformed (invalid assignee): {}", currentLine, assignee);
                continue;
            }
            val id = targetSplitter.nextToken();
            int meta = 0;
            if (count == 2) {
                val metaToken = targetSplitter.nextToken();
                try {
                    meta = Integer.parseInt(metaToken);
                } catch (NumberFormatException e) {
                    Common.LOG.error("ColorConfig line {} malformed (invalid metadata): {}", currentLine, metaToken);
                }
            }
            if (!id.startsWith("__")) {
                val block = GameData.getBlockRegistry().get(id);
                if (block != null && block != Blocks.air) {
                    val cBlock = ((ColoredBlock) block);
                    cBlock.setColoredLightValue(meta, value.r, value.g, value.b);
                }
            }
            valueCache.put(assignee, value);
        }
    }

    private static final RGB EMPTY = new RGB(0, 0, 0);

    private static RGB resolve(String value) {
        if (value.charAt(0) == '@') {
            val key = value.substring(1);
            if (!valueCache.containsKey(key)) {
                Common.LOG.error("ColorConfig line {} malformed (nonexistent backref): {}", currentLine, key);
                return EMPTY;
            }
            return valueCache.get(key);
        }
        if (value.length() != 3) {
            Common.LOG.error("ColorConfig line {} malformed (invalid light value): {}", currentLine, value);
            return EMPTY;
        }
        final int packed;
        try {
            packed = Integer.parseInt(value, 16);
        } catch (NumberFormatException e) {
            Common.LOG.error("ColorConfig line {} malformed (unparseable hex number): {}", currentLine, value);
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
