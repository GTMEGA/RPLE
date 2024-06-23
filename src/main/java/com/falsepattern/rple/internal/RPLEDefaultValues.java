/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal;

import com.falsepattern.rple.api.client.lightmap.RPLELightMapRegistry;
import com.falsepattern.rple.api.client.lightmap.vanilla.BossColorModifierMask;
import com.falsepattern.rple.api.client.lightmap.vanilla.NightVisionMask;
import com.falsepattern.rple.api.client.lightmap.vanilla.VanillaLightMapBase;
import com.falsepattern.rple.api.common.color.DefaultColor;
import com.falsepattern.rple.api.common.color.LightValueColor;
import com.falsepattern.rple.api.common.colorizer.RPLEBlockColorRegistry;
import com.falsepattern.rple.internal.common.config.container.BlockColorConfig;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.block.Block;

import static com.falsepattern.rple.api.common.color.DefaultColor.*;
import static com.falsepattern.rple.api.common.color.LightValueColor.fromVanillaLightOpacity;
import static com.falsepattern.rple.api.common.color.LightValueColor.fromVanillaLightValue;
import static net.minecraft.init.Blocks.*;

@UtilityClass
public final class RPLEDefaultValues {
    public static void registerDefaultLightMaps(RPLELightMapRegistry registry) {
        registry.registerLightMapBase(new VanillaLightMapBase(), 1000);
        registry.registerLightMapMask(new NightVisionMask());
        registry.registerLightMapMask(new BossColorModifierMask());
    }

    public static void preloadDefaultColorPalette(BlockColorConfig config) {
        for (val color : DefaultColor.values())
            config.addPaletteColor(color);
        for (val color : LightValueColor.values())
            config.addPaletteColor(color);
    }

    public static void registerDefaultBlockBrightnessColors(RPLEBlockColorRegistry registry) {
        val blockRegistry = GameData.getBlockRegistry();
        for (val blockObj : blockRegistry) {
            val block = (Block) blockObj;
            val blockId = GameRegistry.findUniqueIdentifierFor(block);
            if (blockId == null)
                continue;
            val modId = blockId.modId;
            if (!"minecraft".equals(modId))
                continue;

            val brightness = fromVanillaLightValue(block.getLightValue());
            registry.colorizeBlock(block).brightness(brightness).apply();
        }

        registry.colorizeBlock(fire).brightness(0xEC0).apply();
        registry.colorizeBlock(lava).brightness(0xF90).apply();
        registry.colorizeBlock(flowing_lava).brightness(0xF90).apply();
        registry.colorizeBlock(glowstone).brightness(0xCC7).apply();
        registry.colorizeBlock(lit_furnace).brightness(0xDCA).apply();
        registry.colorizeBlock(lit_redstone_ore).brightness(0x511).apply();
        registry.colorizeBlock(portal).brightness(0x919).apply();
        registry.colorizeBlock(powered_repeater).brightness(0x611).apply();
        registry.colorizeBlock(redstone_torch).brightness(0x611).apply();
        registry.colorizeBlock(torch).brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock(brown_mushroom).brightness(0x110).apply();
        registry.colorizeBlock(lit_pumpkin).brightness(0xFDB).apply();
        registry.colorizeBlock(brewing_stand).brightness(BLACK).apply();
        registry.colorizeBlock(end_portal).brightness(0x7FB).apply();
        registry.colorizeBlock(end_portal_frame).brightness(0x011).apply();
        registry.colorizeBlock(dragon_egg).brightness(0x011).apply();
        registry.colorizeBlock(lit_redstone_lamp).brightness(0xCC7).apply();
        registry.colorizeBlock(ender_chest).brightness(0x376).apply();
        registry.colorizeBlock(beacon).brightness(0xCCC).apply();
        registry.colorizeBlock(powered_comparator).brightness(0x611).apply();

        registry.colorizeBlock("chisel:futuraCircuit:0").brightness(DIM_BLACK).apply();
        registry.colorizeBlock("chisel:futuraCircuit:1").brightness(DIM_RED).apply();
        registry.colorizeBlock("chisel:futuraCircuit:2").brightness(DIM_GREEN).apply();
        registry.colorizeBlock("chisel:futuraCircuit:3").brightness(DIM_BROWN).apply();
        registry.colorizeBlock("chisel:futuraCircuit:4").brightness(DIM_BLUE).apply();
        registry.colorizeBlock("chisel:futuraCircuit:5").brightness(DIM_PURPLE).apply();
        registry.colorizeBlock("chisel:futuraCircuit:6").brightness(DIM_CYAN).apply();
        registry.colorizeBlock("chisel:futuraCircuit:7").brightness(DIM_GRAY).apply();
        registry.colorizeBlock("chisel:futuraCircuit:8").brightness(DIM_GRAY).apply();
        registry.colorizeBlock("chisel:futuraCircuit:9").brightness(DIM_PINK).apply();
        registry.colorizeBlock("chisel:futuraCircuit:10").brightness(DIM_LIME).apply();
        registry.colorizeBlock("chisel:futuraCircuit:11").brightness(DIM_YELLOW).apply();
        registry.colorizeBlock("chisel:futuraCircuit:12").brightness(DIM_LIGHT_BLUE).apply();
        registry.colorizeBlock("chisel:futuraCircuit:13").brightness(DIM_MAGENTA).apply();
        registry.colorizeBlock("chisel:futuraCircuit:14").brightness(DIM_ORANGE).apply();
        registry.colorizeBlock("chisel:futuraCircuit:15").brightness(DIM_LIGHT_GRAY).apply();

        registry.colorizeBlock("chisel:hexPlating:0").brightness(DIM_BLACK).apply();
        registry.colorizeBlock("chisel:hexPlating:1").brightness(DIM_RED).apply();
        registry.colorizeBlock("chisel:hexPlating:2").brightness(DIM_GREEN).apply();
        registry.colorizeBlock("chisel:hexPlating:3").brightness(DIM_BROWN).apply();
        registry.colorizeBlock("chisel:hexPlating:4").brightness(DIM_BLUE).apply();
        registry.colorizeBlock("chisel:hexPlating:5").brightness(DIM_PURPLE).apply();
        registry.colorizeBlock("chisel:hexPlating:6").brightness(DIM_CYAN).apply();
        registry.colorizeBlock("chisel:hexPlating:7").brightness(DIM_GRAY).apply();
        registry.colorizeBlock("chisel:hexPlating:8").brightness(DIM_GRAY).apply();
        registry.colorizeBlock("chisel:hexPlating:9").brightness(DIM_PINK).apply();
        registry.colorizeBlock("chisel:hexPlating:10").brightness(DIM_LIME).apply();
        registry.colorizeBlock("chisel:hexPlating:11").brightness(DIM_YELLOW).apply();
        registry.colorizeBlock("chisel:hexPlating:12").brightness(DIM_LIGHT_BLUE).apply();
        registry.colorizeBlock("chisel:hexPlating:13").brightness(DIM_MAGENTA).apply();
        registry.colorizeBlock("chisel:hexPlating:14").brightness(DIM_ORANGE).apply();
        registry.colorizeBlock("chisel:hexPlating:15").brightness(DIM_LIGHT_GRAY).apply();

        registry.colorizeBlock("chisel:hexLargePlating:0").brightness(DIM_BLACK).apply();
        registry.colorizeBlock("chisel:hexLargePlating:1").brightness(DIM_RED).apply();
        registry.colorizeBlock("chisel:hexLargePlating:2").brightness(DIM_GREEN).apply();
        registry.colorizeBlock("chisel:hexLargePlating:3").brightness(DIM_BROWN).apply();
        registry.colorizeBlock("chisel:hexLargePlating:4").brightness(DIM_BLUE).apply();
        registry.colorizeBlock("chisel:hexLargePlating:5").brightness(DIM_PURPLE).apply();
        registry.colorizeBlock("chisel:hexLargePlating:6").brightness(DIM_CYAN).apply();
        registry.colorizeBlock("chisel:hexLargePlating:7").brightness(DIM_GRAY).apply();
        registry.colorizeBlock("chisel:hexLargePlating:8").brightness(DIM_GRAY).apply();
        registry.colorizeBlock("chisel:hexLargePlating:9").brightness(DIM_PINK).apply();
        registry.colorizeBlock("chisel:hexLargePlating:10").brightness(DIM_LIME).apply();
        registry.colorizeBlock("chisel:hexLargePlating:11").brightness(DIM_YELLOW).apply();
        registry.colorizeBlock("chisel:hexLargePlating:12").brightness(DIM_LIGHT_BLUE).apply();
        registry.colorizeBlock("chisel:hexLargePlating:13").brightness(DIM_MAGENTA).apply();
        registry.colorizeBlock("chisel:hexLargePlating:14").brightness(DIM_ORANGE).apply();
        registry.colorizeBlock("chisel:hexLargePlating:15").brightness(DIM_LIGHT_GRAY).apply();

        registry.colorizeBlock("chisel:holystone").brightness(DIM_GRAY).apply();
        registry.colorizeBlock("chisel:glowstone").brightness(0xCC7).apply();

        registry.colorizeBlock("chisel:jackolantern1").brightness(0xFDB).apply();
        registry.colorizeBlock("chisel:jackolantern2").brightness(0xFDB).apply();
        registry.colorizeBlock("chisel:jackolantern3").brightness(0xFDB).apply();
        registry.colorizeBlock("chisel:jackolantern4").brightness(0xFDB).apply();
        registry.colorizeBlock("chisel:jackolantern5").brightness(0xFDB).apply();
        registry.colorizeBlock("chisel:jackolantern6").brightness(0xFDB).apply();
        registry.colorizeBlock("chisel:jackolantern7").brightness(0xFDB).apply();
        registry.colorizeBlock("chisel:jackolantern8").brightness(0xFDB).apply();
        registry.colorizeBlock("chisel:jackolantern9").brightness(0xFDB).apply();
        registry.colorizeBlock("chisel:jackolantern10").brightness(0xFDB).apply();
        registry.colorizeBlock("chisel:jackolantern11").brightness(0xFDB).apply();
        registry.colorizeBlock("chisel:jackolantern12").brightness(0xFDB).apply();
        registry.colorizeBlock("chisel:jackolantern13").brightness(0xFDB).apply();
        registry.colorizeBlock("chisel:jackolantern14").brightness(0xFDB).apply();
        registry.colorizeBlock("chisel:jackolantern15").brightness(0xFDB).apply();
        registry.colorizeBlock("chisel:jackolantern16").brightness(0xFDB).apply();

        registry.colorizeBlock("chisel:lavastone").brightness(0xE80).apply();

        registry.colorizeBlock("chisel:torch1").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("chisel:torch2").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("chisel:torch3").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("chisel:torch4").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("chisel:torch5").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("chisel:torch6").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("chisel:torch7").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("chisel:torch8").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("chisel:torch9").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("chisel:torch10").brightness(TORCH_LIGHT).apply();

        registry.colorizeBlock("ExtraUtilities:color_lightgem:0").brightness(WHITE).apply();
        registry.colorizeBlock("ExtraUtilities:color_lightgem:1").brightness(ORANGE).apply();
        registry.colorizeBlock("ExtraUtilities:color_lightgem:2").brightness(MAGENTA).apply();
        registry.colorizeBlock("ExtraUtilities:color_lightgem:3").brightness(LIGHT_BLUE).apply();
        registry.colorizeBlock("ExtraUtilities:color_lightgem:4").brightness(YELLOW).apply();
        registry.colorizeBlock("ExtraUtilities:color_lightgem:5").brightness(LIME).apply();
        registry.colorizeBlock("ExtraUtilities:color_lightgem:6").brightness(PINK).apply();
        registry.colorizeBlock("ExtraUtilities:color_lightgem:7").brightness(GRAY).apply();
        registry.colorizeBlock("ExtraUtilities:color_lightgem:8").brightness(LIGHT_GRAY).apply();
        registry.colorizeBlock("ExtraUtilities:color_lightgem:9").brightness(CYAN).apply();
        registry.colorizeBlock("ExtraUtilities:color_lightgem:10").brightness(PURPLE).apply();
        registry.colorizeBlock("ExtraUtilities:color_lightgem:11").brightness(BLUE).apply();
        registry.colorizeBlock("ExtraUtilities:color_lightgem:12").brightness(BROWN).apply();
        registry.colorizeBlock("ExtraUtilities:color_lightgem:13").brightness(GREEN).apply();
        registry.colorizeBlock("ExtraUtilities:color_lightgem:14").brightness(RED).apply();
        registry.colorizeBlock("ExtraUtilities:color_lightgem:15").brightness(BLACK).apply();

        registry.colorizeBlock("ExtraUtilities:color_redstoneLight:0").brightness(WHITE).apply();
        registry.colorizeBlock("ExtraUtilities:color_redstoneLight:1").brightness(ORANGE).apply();
        registry.colorizeBlock("ExtraUtilities:color_redstoneLight:2").brightness(MAGENTA).apply();
        registry.colorizeBlock("ExtraUtilities:color_redstoneLight:3").brightness(LIGHT_BLUE).apply();
        registry.colorizeBlock("ExtraUtilities:color_redstoneLight:4").brightness(YELLOW).apply();
        registry.colorizeBlock("ExtraUtilities:color_redstoneLight:5").brightness(LIME).apply();
        registry.colorizeBlock("ExtraUtilities:color_redstoneLight:6").brightness(PINK).apply();
        registry.colorizeBlock("ExtraUtilities:color_redstoneLight:7").brightness(GRAY).apply();
        registry.colorizeBlock("ExtraUtilities:color_redstoneLight:8").brightness(LIGHT_GRAY).apply();
        registry.colorizeBlock("ExtraUtilities:color_redstoneLight:9").brightness(CYAN).apply();
        registry.colorizeBlock("ExtraUtilities:color_redstoneLight:10").brightness(PURPLE).apply();
        registry.colorizeBlock("ExtraUtilities:color_redstoneLight:11").brightness(BLUE).apply();
        registry.colorizeBlock("ExtraUtilities:color_redstoneLight:12").brightness(BROWN).apply();
        registry.colorizeBlock("ExtraUtilities:color_redstoneLight:13").brightness(GREEN).apply();
        registry.colorizeBlock("ExtraUtilities:color_redstoneLight:14").brightness(RED).apply();
        registry.colorizeBlock("ExtraUtilities:color_redstoneLight:15").brightness(BLACK).apply();

        registry.colorizeBlock("ExtraUtilities:chandelier").brightness(0xFEB).apply();
        registry.colorizeBlock("ExtraUtilities:magnumTorch").brightness(0xFEB).apply();

        registry.colorizeBlock("Natura:Thornvines").brightness(0x970).apply();

        registry.colorizeBlock("Natura:greenGlowshroom").brightness(0x091).apply();
        registry.colorizeBlock("Natura:purpleGlowshroom").brightness(0x708).apply();
        registry.colorizeBlock("Natura:blueGlowshroom").brightness(0x039).apply();

        registry.colorizeBlock("Natura:Glowshroom:0").brightness(0x091).apply();
        registry.colorizeBlock("Natura:Glowshroom:1").brightness(0x708).apply();
        registry.colorizeBlock("Natura:Glowshroom:2").brightness(0x039).apply();

        registry.colorizeBlock("IC2:blockLuminator").brightness(0xFFE).apply();
        registry.colorizeBlock("AdvancedSolarPanel:BlockMolecularTransformer").brightness(0x0CF).apply();

        registry.colorizeBlock("appliedenergistics2:tile.BlockQuartzLamp").brightness(0xFFF).apply();
        registry.colorizeBlock("appliedenergistics2:tile.BlockQuartzTorch").brightness(0xEEE).apply();

        registry.colorizeBlock("BiomesOPlenty:crystal").brightness(0x0BF).apply();

        registry.colorizeBlock("BuildCraft|Core:markerBlock").brightness(0x037).apply();
        registry.colorizeBlock("BuildCraft|Core:pathMarkerBlock").brightness(0x070).apply();
        registry.colorizeBlock("BuildCraft|Builders:constructionMarkerBlock").brightness(0x750).apply();

        registry.colorizeBlock("Thaumcraft:blockCrystal:0").brightness(0x770).apply();
        registry.colorizeBlock("Thaumcraft:blockCrystal:1").brightness(0x700).apply();
        registry.colorizeBlock("Thaumcraft:blockCrystal:2").brightness(0x007).apply();
        registry.colorizeBlock("Thaumcraft:blockCrystal:3").brightness(0x070).apply();
        registry.colorizeBlock("Thaumcraft:blockCrystal:4").brightness(0x777).apply();
        registry.colorizeBlock("Thaumcraft:blockCrystal:5").brightness(0x444).apply();
        registry.colorizeBlock("Thaumcraft:blockCrystal:6").brightness(0xAAA).apply();

        registry.colorizeBlock("Thaumcraft:blockJar:0").brightness(0x999).apply();
        registry.colorizeBlock("Thaumcraft:blockJar:1").brightness(0x595).apply();

        registry.colorizeBlock("Thaumcraft:blockCandle").brightness(TORCH_LIGHT).apply();

        registry.colorizeBlock("Thaumcraft:blockArcaneFurnace").brightness(0xF90).apply();

        registry.colorizeBlock("Thaumcraft:blockHole").brightness(0xAAA).apply();
        registry.colorizeBlock("Thaumcraft:blockEldritchNothing").brightness(0x333).apply();

        registry.colorizeBlock("Thaumcraft:blockAiry").brightness(0xAAA).apply();
        registry.colorizeBlock("Thaumcraft:blockCustomPlant:5").brightness(0x403).apply();

        registry.colorizeBlock("Railcraft:lantern.stone").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("Railcraft:lantern.metal").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("Railcraft:firestone.recharge").brightness(0xF90).apply();

        registry.colorizeBlock("TwilightForest:tile.TFFirefly").brightness(0x8E0).apply();
        registry.colorizeBlock("TwilightForest:tile.TFPortal").brightness(0x919).apply();
        registry.colorizeBlock("TwilightForest:tile.TFFireflyJar").brightness(0x8E0).apply();
        registry.colorizeBlock("TwilightForest:tile.HugeGloomBlock").brightness(0x420).apply();
        registry.colorizeBlock("TwilightForest:tile.TrollBer").brightness(0xFEC).apply();
        registry.colorizeBlock("TwilightForest:tile.ForceField:0").brightness(0x201).apply();
        registry.colorizeBlock("TwilightForest:tile.ForceField:1").brightness(0x212).apply();
        registry.colorizeBlock("TwilightForest:tile.ForceField:2").brightness(0x210).apply();
        registry.colorizeBlock("TwilightForest:tile.ForceField:3").brightness(0x020).apply();
        registry.colorizeBlock("TwilightForest:tile.ForceField:4").brightness(0x012).apply();
        registry.colorizeBlock("TwilightForest:tile.CinderFurnaceLit").brightness(0xDCA).apply();

        registry.colorizeBlock("Automagy:blockTorchInversion").brightness(0x611).apply();
        registry.colorizeBlock("Automagy:blockCreativeJar").brightness(0x900).apply();
        registry.colorizeBlock("Automagy:blockXPJar").brightness(0x690).apply();

        registry.colorizeBlock("HardcoreEnderExpansion:ravaged_brick_glow").brightness(0xF50).apply();
        registry.colorizeBlock("HardcoreEnderExpansion:essence_altar").brightness(0x600).apply();
        registry.colorizeBlock("HardcoreEnderExpansion:obsidian_special_glow").brightness(0x82F).apply();
        registry.colorizeBlock("HardcoreEnderExpansion:transport_beacon").brightness(0xF0F).apply();
        registry.colorizeBlock("HardcoreEnderExpansion:enhanced_brewing_stand_block").brightness(0x111).apply();
        registry.colorizeBlock("HardcoreEnderExpansion:laser_beam").brightness(0xF8F).apply();
        registry.colorizeBlock("HardcoreEnderExpansion:temple_end_portal").brightness(0x7FB).apply();

        registry.colorizeBlock("Forestry:beehives").brightness(0x431).apply();
        registry.colorizeBlock("ExtraBees:hive").brightness(0x431).apply();
        registry.colorizeBlock("MagicBees:hive").brightness(0x431).apply();

        registry.colorizeBlock("TConstruct:decoration.stonetorch").brightness(TORCH_LIGHT).apply();

        registry.colorizeBlock("EnderIO:blockPaintedGlowstone").brightness(0xCC7).apply();

        registry.colorizeBlock("harvestcraft:pamcandleDeco1").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("harvestcraft:pamcandleDeco2").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("harvestcraft:pamcandleDeco3").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("harvestcraft:pamcandleDeco4").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("harvestcraft:pamcandleDeco5").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("harvestcraft:pamcandleDeco6").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("harvestcraft:pamcandleDeco7").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("harvestcraft:pamcandleDeco8").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("harvestcraft:pamcandleDeco9").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("harvestcraft:pamcandleDeco10").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("harvestcraft:pamcandleDeco11").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("harvestcraft:pamcandleDeco12").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("harvestcraft:pamcandleDeco13").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("harvestcraft:pamcandleDeco14").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("harvestcraft:pamcandleDeco15").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("harvestcraft:pamcandleDeco16").brightness(TORCH_LIGHT).apply();

        registry.colorizeBlock("thaumicbases:crystalBlock:0").brightness(0x770).apply();
        registry.colorizeBlock("thaumicbases:crystalBlock:1").brightness(0x700).apply();
        registry.colorizeBlock("thaumicbases:crystalBlock:2").brightness(0x007).apply();
        registry.colorizeBlock("thaumicbases:crystalBlock:3").brightness(0x070).apply();
        registry.colorizeBlock("thaumicbases:crystalBlock:4").brightness(0x777).apply();
        registry.colorizeBlock("thaumicbases:crystalBlock:5").brightness(0x444).apply();
        registry.colorizeBlock("thaumicbases:crystalBlock:6").brightness(0xAAA).apply();
        registry.colorizeBlock("thaumicbases:crystalBlock:7").brightness(0x707).apply();

        registry.colorizeBlock("thaumicbases:pyrofluid").brightness(0xFF7).apply();
        registry.colorizeBlock("thaumicbases:campfire").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("thaumicbases:brazier").brightness(0x777).apply();

        registry.colorizeBlock("thaumicbases:crystalSlab:0").brightness(0x770).apply();
        registry.colorizeBlock("thaumicbases:crystalSlab:1").brightness(0x700).apply();
        registry.colorizeBlock("thaumicbases:crystalSlab:2").brightness(0x007).apply();
        registry.colorizeBlock("thaumicbases:crystalSlab:3").brightness(0x070).apply();
        registry.colorizeBlock("thaumicbases:crystalSlab:4").brightness(0x777).apply();
        registry.colorizeBlock("thaumicbases:crystalSlab:5").brightness(0x444).apply();
        registry.colorizeBlock("thaumicbases:crystalSlab:6").brightness(0xAAA).apply();
        registry.colorizeBlock("thaumicbases:crystalSlab:7").brightness(0x707).apply();

        registry.colorizeBlock("thaumicbases:crystalSlab_full:0").brightness(0x770).apply();
        registry.colorizeBlock("thaumicbases:crystalSlab_full:1").brightness(0x700).apply();
        registry.colorizeBlock("thaumicbases:crystalSlab_full:2").brightness(0x007).apply();
        registry.colorizeBlock("thaumicbases:crystalSlab_full:3").brightness(0x070).apply();
        registry.colorizeBlock("thaumicbases:crystalSlab_full:4").brightness(0x777).apply();
        registry.colorizeBlock("thaumicbases:crystalSlab_full:5").brightness(0x444).apply();
        registry.colorizeBlock("thaumicbases:crystalSlab_full:6").brightness(0xAAA).apply();
        registry.colorizeBlock("thaumicbases:crystalSlab_full:7").brightness(0x707).apply();

        registry.colorizeBlock("thaumicbases:aureliaPetal").brightness(0x424).apply();
        registry.colorizeBlock("thaumicbases:aurelia").brightness(0x747).apply();

        registry.colorizeBlock("witchery:witchweb").brightness(0x101).apply();
        registry.colorizeBlock("witchery:glintweed").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("witchery:leapinglily").brightness(0x363).apply();
        registry.colorizeBlock("witchery:embermoss").brightness(0x630).apply();
        registry.colorizeBlock("witchery:demonheart").brightness(0x300).apply();
        registry.colorizeBlock("witchery:candelabra").brightness(0xFEB).apply();
        registry.colorizeBlock("witchery:alluringskull").brightness(0x707).apply();
        registry.colorizeBlock("witchery:glowglobe").brightness(TORCH_LIGHT).apply();
        registry.colorizeBlock("witchery:infinityegg").brightness(0x100).apply();
        registry.colorizeBlock("witchery:spiritportal").brightness(0x55B).apply();
        registry.colorizeBlock("witchery:tormentportal").brightness(0xB40).apply();
        registry.colorizeBlock("witchery:witchesovenburning").brightness(0xDCA).apply();
        registry.colorizeBlock("witchery:distilleryburning").brightness(0x626).apply();
        registry.colorizeBlock("witchery:light").brightness(0xFFF).apply();
        registry.colorizeBlock("witchery:mirrorblock").brightness(0xCCC).apply();
        registry.colorizeBlock("witchery:mirrorblock2").brightness(0xCCC).apply();

        registry.colorizeBlock("Ztones:lampf").brightness(0xFFE).apply();
        registry.colorizeBlock("Ztones:lampt").brightness(0xDDC).apply();
        registry.colorizeBlock("Ztones:lampb").brightness(0xAA9).apply();

        registry.colorizeBlock("ThaumicTinkerer:gaseousLight").brightness(0xCCC).apply();

        registry.colorizeBlock("BiblioCraft:BiblioDesk").brightness(0x775).apply();
        registry.colorizeBlock("BiblioCraft:BiblioLantern").brightness(0xFED).apply();
        registry.colorizeBlock("BiblioCraft:BiblioIronLantern").brightness(0xFED).apply();
        registry.colorizeBlock("BiblioCraft:BiblioLamp").brightness(0xFFF).apply();
        registry.colorizeBlock("BiblioCraft:BiblioIronLamp").brightness(0xFFF).apply();

        registry.colorizeBlock("BiomesOPlenty:coral1:15").brightness(0x708).apply();

        registry.colorizeBlock("BiblioWoodsBoP:BiblioWooddesk").brightness(0x775).apply();
        registry.colorizeBlock("BiblioWoodsForestry:BiblioWoodFstdesk").brightness(0x775).apply();
        registry.colorizeBlock("BiblioWoodsForestry:BiblioWoodFstdesk2").brightness(0x775).apply();
        registry.colorizeBlock("BiblioWoodsNatura:BiblioWooddesk").brightness(0x775).apply();

        registry.colorizeBlock("BloodArsenal:blood_torch").brightness(0x300).apply();
        registry.colorizeBlock("BloodArsenal:blood_infused_glowstone").brightness(0xF00).apply();
        registry.colorizeBlock("BloodArsenal:blood_lamp").brightness(0xF00).apply();

        registry.colorizeBlock("CarpentersBlocks:blockCarpentersTorch").brightness(TORCH_LIGHT).apply();

        registry.colorizeBlock("OpenComputers:assembler").brightness(0x055).apply();
        registry.colorizeBlock("OpenComputers:capacitor").brightness(0x052).apply();
        registry.colorizeBlock("OpenComputers:geolyzer").brightness(0x320).apply();
        registry.colorizeBlock("OpenComputers:hologram1").brightness(0xFFF).apply();
        registry.colorizeBlock("OpenComputers:hologram2").brightness(0xFFF).apply();
        registry.colorizeBlock("OpenComputers:powerDistributor").brightness(0x550).apply();
        registry.colorizeBlock("OpenComputers:screen1").brightness(0xCCC).apply();
        registry.colorizeBlock("OpenComputers:screen2").brightness(0xCCC).apply();
        registry.colorizeBlock("OpenComputers:screen3").brightness(0xCCC).apply();
        registry.colorizeBlock("OpenComputers:carpetedCapacitor").brightness(0x052).apply();
        registry.colorizeBlock("OpenComputers:case1").brightness(0xCCC).apply();
        registry.colorizeBlock("OpenComputers:case2").brightness(0xCCC).apply();
        registry.colorizeBlock("OpenComputers:case3").brightness(0xCCC).apply();
        registry.colorizeBlock("OpenComputers:caseCreative").brightness(0xCCC).apply();

        registry.colorizeBlock("computronics:computronics.colorfulLamp").brightness(0xAAA).apply();

        registry.colorizeBlock("DraconicEvolution:potentiometer").brightness(0x444).apply();
        registry.colorizeBlock("DraconicEvolution:safetyFlame").brightness(0xEC0).apply();

        registry.colorizeBlock("gadomancy:BlockStickyJar").brightness(0x999).apply();
        registry.colorizeBlock("gadomancy:BlockRemoteJar").brightness(0x999).apply();
        registry.colorizeBlock("gadomancy:BlockExtendedNodeJar").brightness(0x999).apply();

        registry.colorizeBlock("ThaumicExploration:trashJar").brightness(0x999).apply();
        registry.colorizeBlock("ThaumicExploration:boundJar").brightness(0x999).apply();
        registry.colorizeBlock("ThaumicExploration:thinkTankJar").brightness(0x999).apply();
        registry.colorizeBlock("ThaumicExploration:floatCandle").brightness(TORCH_LIGHT).apply();

        registry.colorizeBlock("OpenBlocks:guide").brightness(0x999).apply();
        registry.colorizeBlock("OpenBlocks:builder_guide").brightness(0x999).apply();
        registry.colorizeBlock("OpenBlocks:target").brightness(0x444).apply();

        registry.colorizeBlock("harvestthenether:glowFlower").brightness(0xCC7).apply();
        registry.colorizeBlock("harvestthenether:glowflowerCrop").brightness(0xCC7).apply();

        registry.colorizeBlock("RandomThings:spectreBlock").brightness(0xFFF).apply();
        registry.colorizeBlock("TMechworks:Dynamo").brightness(0xFFF).apply();

        registry.colorizeBlock("lotr:tile.woodElvenTorch").brightness(0xEAA).apply();
        registry.colorizeBlock("lotr:tile.highElvenTorch").brightness(0xADC).apply();
        registry.colorizeBlock("lotr:tile.morgulTorch").brightness(0x8DB).apply();
        registry.colorizeBlock("lotr:tile.mallornTorch").brightness(0xEEF).apply();
        registry.colorizeBlock("lotr:tile.mallornTorchBlue").brightness(0xADD).apply();
        registry.colorizeBlock("lotr:tile.mallornTorchGold").brightness(0xED4).apply();
        registry.colorizeBlock("lotr:tile.mallornTorchGreen").brightness(0x4E8).apply();

        registry.colorizeBlock("lotr:tile.orcTorch:0").brightness(0x000).apply();
        registry.colorizeBlock("lotr:tile.orcTorch:1").brightness(0xEEB).apply();
        registry.colorizeBlock("lotr:tile.tauredainDoubleTorch:0").brightness(0x000).apply();
        registry.colorizeBlock("lotr:tile.tauredainDoubleTorch:1").brightness(0xEEB).apply();

        registry.colorizeBlock("lotr:tile.chandelier:0").brightness(0xEEE).apply();
        registry.colorizeBlock("lotr:tile.chandelier:1").brightness(0xEEE).apply();
        registry.colorizeBlock("lotr:tile.chandelier:2").brightness(0xEEE).apply();
        registry.colorizeBlock("lotr:tile.chandelier:3").brightness(0xEEE).apply();
        registry.colorizeBlock("lotr:tile.chandelier:4").brightness(0xEEE).apply();
        registry.colorizeBlock("lotr:tile.chandelier:5").brightness(0xEEE).apply();
        registry.colorizeBlock("lotr:tile.chandelier:6").brightness(0xEEE).apply();
        registry.colorizeBlock("lotr:tile.chandelier:7").brightness(0xEEE).apply();
        registry.colorizeBlock("lotr:tile.chandelier:8").brightness(0xEEE).apply();
        registry.colorizeBlock("lotr:tile.chandelier:9").brightness(0xEEE).apply();
        registry.colorizeBlock("lotr:tile.chandelier:10").brightness(0xEEE).apply();
        registry.colorizeBlock("lotr:tile.chandelier:11").brightness(0xEEE).apply();
        registry.colorizeBlock("lotr:tile.chandelier:12").brightness(0xEEE).apply();
        registry.colorizeBlock("lotr:tile.chandelier:13").brightness(0xEEE).apply();
        registry.colorizeBlock("lotr:tile.chandelier:14").brightness(0xEEE).apply();
        registry.colorizeBlock("lotr:tile.chandelier:15").brightness(0xEEE).apply();

        registry.colorizeBlock("lotr:tile.rhunFire").brightness(0xFFF).apply();

        registry.colorizeBlock("lotr:tile.elvenPortal").brightness(0xDDD).apply();
        registry.colorizeBlock("lotr:tile.morgulPortal").brightness(0xDDD).apply();
        registry.colorizeBlock("lotr:tile.utumnoReturnPortal").brightness(0xFFF).apply();
        registry.colorizeBlock("lotr:tile.utumnoReturnLight").brightness(0xFFF).apply();

        registry.colorizeBlock("lotr:tile.oreNaurite").brightness(0x777).apply();
        registry.colorizeBlock("lotr:tile.oreQuendite").brightness(0xBBB).apply();
        registry.colorizeBlock("lotr:tile.oreGlowstone").brightness(0xBBB).apply();

        registry.colorizeBlock("lotr:tile.oreGulduril:0").brightness(0xBBB).apply();
        registry.colorizeBlock("lotr:tile.oreGulduril:1").brightness(0xBBB).apply();

        registry.colorizeBlock("lotr:tile.morgulCraftingTable:1").brightness(0x777).apply();
        registry.colorizeBlock("lotr:tile.corruptMallorn:1").brightness(0x999).apply();
        registry.colorizeBlock("lotr:tile.hithlainLadder:1").brightness(0x555).apply();

        registry.colorizeBlock("lotr:tile.guldurilBrick:0").brightness(0xBBB).apply();
        registry.colorizeBlock("lotr:tile.guldurilBrick:1").brightness(0xBBB).apply();
        registry.colorizeBlock("lotr:tile.guldurilBrick:2").brightness(0xBBB).apply();
        registry.colorizeBlock("lotr:tile.guldurilBrick:3").brightness(0xBBB).apply();
        registry.colorizeBlock("lotr:tile.guldurilBrick:4").brightness(0xBBB).apply();
        registry.colorizeBlock("lotr:tile.guldurilBrick:5").brightness(0xBBB).apply();
        registry.colorizeBlock("lotr:tile.guldurilBrick:6").brightness(0xBBB).apply();
        registry.colorizeBlock("lotr:tile.guldurilBrick:7").brightness(0xBBB).apply();
        registry.colorizeBlock("lotr:tile.guldurilBrick:8").brightness(0xBBB).apply();
        registry.colorizeBlock("lotr:tile.guldurilBrick:9").brightness(0xBBB).apply();

        registry.colorizeBlock("etfuturum:sea_lantern").brightness(0xAFF).apply();
        registry.colorizeBlock("etfuturum:end_rod").brightness(0xFAF).apply();
        registry.colorizeBlock("etfuturum:magma").brightness(0x510).apply();
        registry.colorizeBlock("etfuturum:beacon").brightness(0xCCC).apply();
        registry.colorizeBlock("etfuturum:deepslate_lit_redstone_ore").brightness(0x511).apply();
        registry.colorizeBlock("etfuturum:lantern").brightness(0xFC5).apply();
        registry.colorizeBlock("etfuturum:lit_blast_furnace").brightness(0xDCA).apply();
        registry.colorizeBlock("etfuturum:lit_smoker").brightness(0xDCA).apply();

        registry.colorizeBlock("etfuturum:amethyst_cluster_1:0").brightness(0x101).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_1:1").brightness(0x101).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_1:2").brightness(0x101).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_1:3").brightness(0x101).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_1:4").brightness(0x101).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_1:5").brightness(0x101).apply();

        registry.colorizeBlock("etfuturum:amethyst_cluster_1:6").brightness(0x212).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_1:7").brightness(0x212).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_1:8").brightness(0x212).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_1:9").brightness(0x212).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_1:10").brightness(0x212).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_1:11").brightness(0x212).apply();

        registry.colorizeBlock("etfuturum:amethyst_cluster_2:0").brightness(0x324).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_2:1").brightness(0x324).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_2:2").brightness(0x324).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_2:3").brightness(0x324).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_2:4").brightness(0x324).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_2:5").brightness(0x324).apply();

        registry.colorizeBlock("etfuturum:amethyst_cluster_2:6").brightness(0x435).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_2:7").brightness(0x435).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_2:8").brightness(0x435).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_2:9").brightness(0x435).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_2:10").brightness(0x435).apply();
        registry.colorizeBlock("etfuturum:amethyst_cluster_2:11").brightness(0x435).apply();
    }

    public static void registerDefaultBlockTranslucencyColors(RPLEBlockColorRegistry registry) {
        val blockRegistry = GameData.getBlockRegistry();
        for (val blockObj : blockRegistry) {
            val block = (Block) blockObj;
            val blockId = GameRegistry.findUniqueIdentifierFor(block);
            if (!"minecraft".equals(blockId.modId))
                continue;
            val translucency = fromVanillaLightOpacity(block.getLightOpacity());
            registry.colorizeBlock(block).translucency(translucency).apply();
        }

        registry.colorizeBlock(stained_glass, 0).translucency(WHITE).apply();
        registry.colorizeBlock(stained_glass, 1).translucency(ORANGE).apply();
        registry.colorizeBlock(stained_glass, 2).translucency(MAGENTA).apply();
        registry.colorizeBlock(stained_glass, 3).translucency(LIGHT_BLUE).apply();
        registry.colorizeBlock(stained_glass, 4).translucency(YELLOW).apply();
        registry.colorizeBlock(stained_glass, 5).translucency(LIME).apply();
        registry.colorizeBlock(stained_glass, 6).translucency(PINK).apply();
        registry.colorizeBlock(stained_glass, 7).translucency(GRAY).apply();
        registry.colorizeBlock(stained_glass, 8).translucency(LIGHT_GRAY).apply();
        registry.colorizeBlock(stained_glass, 9).translucency(CYAN).apply();
        registry.colorizeBlock(stained_glass, 10).translucency(PURPLE).apply();
        registry.colorizeBlock(stained_glass, 11).translucency(BLUE).apply();
        registry.colorizeBlock(stained_glass, 12).translucency(BROWN).apply();
        registry.colorizeBlock(stained_glass, 13).translucency(GREEN).apply();
        registry.colorizeBlock(stained_glass, 14).translucency(RED).apply();
        registry.colorizeBlock(stained_glass, 15).translucency(BLACK).apply();

        registry.colorizeBlock(stained_glass_pane, 0).translucency(WHITE).apply();
        registry.colorizeBlock(stained_glass_pane, 1).translucency(ORANGE).apply();
        registry.colorizeBlock(stained_glass_pane, 2).translucency(MAGENTA).apply();
        registry.colorizeBlock(stained_glass_pane, 3).translucency(LIGHT_BLUE).apply();
        registry.colorizeBlock(stained_glass_pane, 4).translucency(YELLOW).apply();
        registry.colorizeBlock(stained_glass_pane, 5).translucency(LIME).apply();
        registry.colorizeBlock(stained_glass_pane, 6).translucency(PINK).apply();
        registry.colorizeBlock(stained_glass_pane, 7).translucency(GRAY).apply();
        registry.colorizeBlock(stained_glass_pane, 8).translucency(LIGHT_GRAY).apply();
        registry.colorizeBlock(stained_glass_pane, 9).translucency(CYAN).apply();
        registry.colorizeBlock(stained_glass_pane, 10).translucency(PURPLE).apply();
        registry.colorizeBlock(stained_glass_pane, 11).translucency(BLUE).apply();
        registry.colorizeBlock(stained_glass_pane, 12).translucency(BROWN).apply();
        registry.colorizeBlock(stained_glass_pane, 13).translucency(GREEN).apply();
        registry.colorizeBlock(stained_glass_pane, 14).translucency(RED).apply();
        registry.colorizeBlock(stained_glass_pane, 15).translucency(BLACK).apply();

        registry.colorizeBlock("etfuturum:slime").translucency(LIME).apply();
    }
}
