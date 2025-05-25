/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
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

import com.falsepattern.falsetweaks.api.dynlights.FTDynamicLights;
import com.falsepattern.rple.api.common.ServerColorHelper;
import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.color.DefaultColor;
import com.falsepattern.rple.api.common.color.LightValueColor;
import com.falsepattern.rple.api.common.entity.RPLECustomEntityBrightness;
import com.falsepattern.rple.api.common.event.EntityColorRegistrationEvent;
import com.falsepattern.rple.api.common.event.ItemColorRegistrationEvent;
import com.falsepattern.rple.api.common.item.RPLECustomItemBrightness;
import com.falsepattern.rple.internal.HardcoreDarkness;
import com.falsepattern.rple.internal.client.dynlights.ColorDynamicLights;
import com.falsepattern.rple.internal.client.lamp.LampRenderer;
import com.falsepattern.rple.internal.client.render.VertexConstants;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import lombok.NoArgsConstructor;
import lombok.val;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static com.falsepattern.rple.internal.client.lightmap.LightMapPipeline.lightMapPipeline;

@NoArgsConstructor
public final class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent evt) {
        super.preInit(evt);
        VertexConstants.initVertexConstants();
        FMLCommonHandler.instance().bus().register(this);
        FMLCommonHandler.instance().bus().register(HardcoreDarkness.INSTANCE);
    }

    @Override
    public void init(FMLInitializationEvent evt) {
        super.init(evt);
        RenderingRegistry.registerBlockHandler(new LampRenderer());
        lightMapPipeline().registerLightMapProviders();
        FTDynamicLights.registerBackend(ColorDynamicLights.INSTANCE, 500);
    }

    @SubscribeEvent
    public void handleItemColors(ItemColorRegistrationEvent e) {
        e.registry().registerItemColorCallback(stack -> {
            val item = stack.getItem();
            if (item instanceof RPLECustomItemBrightness) {
                val c = ((RPLECustomItemBrightness)item).rple$getCustomBrightnessColor(stack);
                if (c != -1) {
                    return c;
                }
            }
            if (item == Items.lava_bucket) {
                return RPLEBlock.of(Blocks.lava).rple$getBrightnessColor();
            } else if (item == Items.blaze_rod || item == Items.blaze_powder) {
                return DefaultColor.DIM_ORANGE.rgb16();
            } else if (item == Items.glowstone_dust) {
                return DefaultColor.DIM_YELLOW.rgb16();
            } else if (item == Items.magma_cream) {
                return DefaultColor.DIM_RED.rgb16();
            } else if (item == Items.nether_star) {
                return RPLEBlock.of(Blocks.beacon).rple$getBrightnessColor();
            }
            return (short) -1;
        }, 0);
    }

    @SubscribeEvent
    public void handleEntityColors(EntityColorRegistrationEvent e) {
        e.registry().registerEntityColorCallback((entity, forWorldMesh) -> {
            if (entity == Minecraft.getMinecraft().renderViewEntity && !FTDynamicLights.isDynamicHandLight(forWorldMesh)) {
                return LightValueColor.LIGHT_VALUE_0.rgb16();
            }
            if (entity instanceof RPLECustomEntityBrightness) {
                val c = ((RPLECustomEntityBrightness)entity).rple$getCustomBrightnessColor();
                if (c != -1) {
                    return c;
                }
            }
            if (entity instanceof EntityItem) {
                EntityItem entityItem = (EntityItem) entity;
                ItemStack itemStack = ColorDynamicLights.getItemStack(entityItem);
                return ColorDynamicLights.getLightLevel(itemStack);
            }
            if (entity.isBurning()) {
                return RPLEBlock.of(Blocks.fire).rple$getBrightnessColor();
            }
            if (entity instanceof EntityFireball) {
                return RPLEBlock.of(Blocks.fire).rple$getBrightnessColor();
            }
            if (entity instanceof EntityTNTPrimed) {
                return RPLEBlock.of(Blocks.fire).rple$getBrightnessColor();
            }
            if (entity instanceof EntityBlaze) {
                EntityBlaze entityBlaze = (EntityBlaze) entity;
                return entityBlaze.func_70845_n() ? RPLEBlock.of(Blocks.fire).rple$getBrightnessColor()
                                                  : RPLEBlock.of(Blocks.lava).rple$getBrightnessColor();
            }
            if (entity instanceof EntityMagmaCube) {
                EntityMagmaCube emc = (EntityMagmaCube) entity;
                return (double) emc.squishFactor > 0.6 ? RPLEBlock.of(Blocks.fire).rple$getBrightnessColor()
                                                       : RPLEBlock.of(Blocks.lava).rple$getBrightnessColor();
            }
            if (entity instanceof EntityCreeper) {
                EntityCreeper entityCreeper = (EntityCreeper) entity;
                if (entityCreeper.getCreeperState() > 0) {
                    return RPLEBlock.of(Blocks.fire).rple$getBrightnessColor();
                }
            }
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase player = (EntityLivingBase) entity;
                ItemStack stackMain = player.getHeldItem();
                val levelMain = ColorDynamicLights.getLightLevel(stackMain);
                ItemStack stackHead = player.getEquipmentInSlot(4);
                val levelHead = ColorDynamicLights.getLightLevel(stackHead);
                return ServerColorHelper.RGB16FromRGBChannel4Bit(
                        Math.max(ServerColorHelper.red(levelMain), ServerColorHelper.red(levelHead)),
                        Math.max(ServerColorHelper.green(levelMain), ServerColorHelper.green(levelHead)),
                        Math.max(ServerColorHelper.blue(levelMain), ServerColorHelper.blue(levelHead))
                                                                );
            }
            if (entity instanceof EntityItemFrame) {
                EntityItemFrame entityItemFrame = (EntityItemFrame) entity;
                ItemStack itemStack = entityItemFrame.getDisplayedItem();
                return ColorDynamicLights.getLightLevel(itemStack);
            }
            return (short) -1;
        }, 0);
    }
}
