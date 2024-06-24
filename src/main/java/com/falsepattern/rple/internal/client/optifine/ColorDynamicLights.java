/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package com.falsepattern.rple.internal.client.optifine;

import com.falsepattern.rple.api.client.CookieMonster;
import com.falsepattern.rple.api.client.ClientColorHelper;
import com.falsepattern.rple.api.common.ServerColorHelper;
import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.color.DefaultColor;
import com.falsepattern.rple.api.common.color.LightValueColor;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import stubpackage.Config;

import java.util.List;

public class ColorDynamicLights {
    private static final ColorDynamicLightsMap mapDynamicLights = new ColorDynamicLightsMap();
    private static long timeUpdateMs = 0L;

    public ColorDynamicLights() {
    }

    public static void entityAdded(Entity entityIn, RenderGlobal renderGlobal) {
    }

    public static void entityRemoved(Entity entityIn, RenderGlobal renderGlobal) {
        synchronized (mapDynamicLights) {
            ColorDynamicLight dynamicLight = mapDynamicLights.remove(entityIn.getEntityId());
            if (dynamicLight != null) {
                dynamicLight.updateLitChunks(renderGlobal);
            }
        }
    }

    public static void update(RenderGlobal renderGlobal) {
        long timeNowMs = System.currentTimeMillis();
        if (timeNowMs < timeUpdateMs + 50L) {
            return;
        }
        timeUpdateMs = timeNowMs;
        synchronized (mapDynamicLights) {
            updateMapDynamicLights(renderGlobal);
            if (mapDynamicLights.size() <= 0) {
                return;
            }
            List<ColorDynamicLight> dynamicLights = mapDynamicLights.valueList();

            for (int i = 0; i < dynamicLights.size(); ++i) {
                ColorDynamicLight dynamicLight = dynamicLights.get(i);
                dynamicLight.update(renderGlobal);
            }
        }
    }

    private static void updateMapDynamicLights(RenderGlobal renderGlobal) {
        World world = renderGlobal.theWorld;
        if (world != null) {
            for (Entity entity : (List<Entity>) world.getLoadedEntityList()) {
                val lightLevel = getLightLevel(entity);
                if (ServerColorHelper.red(lightLevel) > 0 || ServerColorHelper.green(lightLevel) > 0 || ServerColorHelper.blue(lightLevel) > 0) {
                    int key = entity.getEntityId();
                    ColorDynamicLight dynamicLight = mapDynamicLights.get(key);
                    if (dynamicLight == null) {
                        dynamicLight = new ColorDynamicLight(entity);
                        mapDynamicLights.put(key, dynamicLight);
                    }
                } else {
                    int key = entity.getEntityId();
                    ColorDynamicLight dynamicLight = mapDynamicLights.remove(key);
                    if (dynamicLight != null) {
                        dynamicLight.updateLitChunks(renderGlobal);
                    }
                }
            }
        }
    }

    public static int getCombinedLight(int x, int y, int z, int combinedLight) {
        val lightPlayer = getLightLevel(x, y, z);
        return getCombinedLight(lightPlayer, combinedLight);
    }

    public static int getCombinedLight(Entity entity, int combinedLight) {
        val lightPlayer = getLightLevel(entity);
        return getCombinedLight(new DoubleBrightness(ServerColorHelper.red(lightPlayer),
                        ServerColorHelper.green(lightPlayer),
                        ServerColorHelper.blue(lightPlayer)),
                                combinedLight);
    }

    private static int getCombinedLight(DoubleBrightness lightPlayer, int combinedLight) {
        if (lightPlayer.r > 0 || lightPlayer.g > 0 || lightPlayer.b > 0) {
            long splitCombined = CookieMonster.RGB64FromCookie(combinedLight);
            int combinedR = ClientColorHelper.vanillaFromRGB64Red(splitCombined);
            int combinedG = ClientColorHelper.vanillaFromRGB64Green(splitCombined);
            int combinedB = ClientColorHelper.vanillaFromRGB64Blue(splitCombined);
            int combinedSr = ClientColorHelper.sky8BitFromVanilla(combinedR);
            int combinedSg = ClientColorHelper.sky8BitFromVanilla(combinedG);
            int combinedSb = ClientColorHelper.sky8BitFromVanilla(combinedB);
            int combinedBr = ClientColorHelper.block8BitFromVanilla(combinedR);
            int combinedBg = ClientColorHelper.block8BitFromVanilla(combinedG);
            int combinedBb = ClientColorHelper.block8BitFromVanilla(combinedB);
            int lightPlayerR = (int) (lightPlayer.r * 16.0);
            int lightPlayerG = (int) (lightPlayer.g * 16.0);
            int lightPlayerB = (int) (lightPlayer.b * 16.0);
            if (lightPlayerR > combinedBr) {
                combinedBr = lightPlayerR;
            }
            if (lightPlayerG > combinedBg) {
                combinedBg = lightPlayerG;
            }
            if (lightPlayerB > combinedBb) {
                combinedBb = lightPlayerB;
            }
            combinedLight = CookieMonster.cookieFromRGB64(
                    ClientColorHelper.RGB64FromVanillaRGB(
                            ClientColorHelper.vanillaFromBlockSky8Bit(combinedBr, combinedSr),
                            ClientColorHelper.vanillaFromBlockSky8Bit(combinedBg, combinedSg),
                            ClientColorHelper.vanillaFromBlockSky8Bit(combinedBb, combinedSb)));
        }

        return combinedLight;
    }

    private static DoubleBrightness getLightLevel(int x, int y, int z) {
        double lightLevelMaxR = 0.0;
        double lightLevelMaxG = 0.0;
        double lightLevelMaxB = 0.0;
        synchronized (mapDynamicLights) {
            List<ColorDynamicLight> dynamicLights = mapDynamicLights.valueList();

            for (int i = 0; i < dynamicLights.size(); ++i) {
                ColorDynamicLight dynamicLight = dynamicLights.get(i);
                var dynamicLightLevel = dynamicLight.getLastLightLevel();
                double dynamicR = ServerColorHelper.red(dynamicLightLevel);
                double dynamicG = ServerColorHelper.green(dynamicLightLevel);
                double dynamicB = ServerColorHelper.blue(dynamicLightLevel);
                if (dynamicR > 0 || dynamicG > 0 || dynamicB > 0) {
                    double px = dynamicLight.getLastPosX();
                    double py = dynamicLight.getLastPosY();
                    double pz = dynamicLight.getLastPosZ();
                    double dx = (double) x - px;
                    double dy = (double) y - py;
                    double dz = (double) z - pz;
                    double distSq = dx * dx + dy * dy + dz * dz;
                    if (dynamicLight.isUnderwater() && !Config.isClearWater()) {
                        dynamicR = Config.limit(dynamicR - 2, 0, 15);
                        dynamicG = Config.limit(dynamicG - 2, 0, 15);
                        dynamicB = Config.limit(dynamicB - 2, 0, 15);
                        distSq *= 2.0;
                    }

                    double lightDist = Math.max(Math.max(dynamicR, dynamicG), dynamicB);

                    if (distSq <= (lightDist * lightDist)) {
                        double dist = Math.sqrt(distSq);
                        double light = 1.0 - dist / lightDist;
                        double lightLevelR = light * dynamicR;
                        double lightLevelG = light * dynamicG;
                        double lightLevelB = light * dynamicB;
                        if (lightLevelR > lightLevelMaxR) {
                            lightLevelMaxR = lightLevelR;
                        }
                        if (lightLevelG > lightLevelMaxG) {
                            lightLevelMaxG = lightLevelG;
                        }
                        if (lightLevelB > lightLevelMaxB) {
                            lightLevelMaxB = lightLevelB;
                        }
                    }
                }
            }
        }

        lightLevelMaxR = Config.limit(lightLevelMaxR, 0, 15);
        lightLevelMaxG = Config.limit(lightLevelMaxG, 0, 15);
        lightLevelMaxB = Config.limit(lightLevelMaxB, 0, 15);
        return new DoubleBrightness(lightLevelMaxR, lightLevelMaxG, lightLevelMaxB);
    }

    public static short getLightLevel(ItemStack itemStack) {
        if (itemStack == null) {
            return LightValueColor.LIGHT_VALUE_0.rgb16();
        } else {
            Item item = itemStack.getItem();
            if (item instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock) item;
                Block block = itemBlock.field_150939_a;
                if (block != null) {
                    return RPLEBlock.of(block).rple$getBrightnessColor(itemStack.getItemDamage());
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
            } else {
                return item == Items.nether_star ? RPLEBlock.of(Blocks.beacon).rple$getBrightnessColor()
                                                 : LightValueColor.LIGHT_VALUE_0.rgb16();
            }
        }
    }

    public static short getLightLevel(Entity entity) {
        if (entity == Minecraft.getMinecraft().renderViewEntity && !Config.isDynamicHandLight()) {
            return LightValueColor.LIGHT_VALUE_0.rgb16();
        } else if (entity.isBurning()) {
            return RPLEBlock.of(Blocks.fire).rple$getBrightnessColor();
        } else if (entity instanceof EntityFireball) {
            return RPLEBlock.of(Blocks.fire).rple$getBrightnessColor();
        } else if (entity instanceof EntityTNTPrimed) {
            return RPLEBlock.of(Blocks.fire).rple$getBrightnessColor();
        } else if (entity instanceof EntityBlaze) {
            EntityBlaze entityBlaze = (EntityBlaze) entity;
            return entityBlaze.func_70845_n() ? RPLEBlock.of(Blocks.fire).rple$getBrightnessColor()
                                              : RPLEBlock.of(Blocks.lava).rple$getBrightnessColor();
        } else if (entity instanceof EntityMagmaCube) {
            EntityMagmaCube emc = (EntityMagmaCube) entity;
            return (double) emc.squishFactor > 0.6 ? RPLEBlock.of(Blocks.fire).rple$getBrightnessColor()
                                                   : RPLEBlock.of(Blocks.lava).rple$getBrightnessColor();
        } else {
            if (entity instanceof EntityCreeper) {
                EntityCreeper entityCreeper = (EntityCreeper) entity;
                if (entityCreeper.getCreeperState() > 0) {
                    return RPLEBlock.of(Blocks.fire).rple$getBrightnessColor();
                }
            }

            if (entity instanceof EntityLivingBase) {
                EntityLivingBase player = (EntityLivingBase) entity;
                ItemStack stackMain = player.getHeldItem();
                val levelMain = getLightLevel(stackMain);
                ItemStack stackHead = player.getEquipmentInSlot(4);
                val levelHead = getLightLevel(stackHead);
                return ServerColorHelper.RGB16FromRGBChannel4Bit(
                        Math.max(ServerColorHelper.red(levelMain), ServerColorHelper.red(levelHead)),
                        Math.max(ServerColorHelper.green(levelMain), ServerColorHelper.green(levelHead)),
                        Math.max(ServerColorHelper.blue(levelMain), ServerColorHelper.blue(levelHead))
                );
            } else if (entity instanceof EntityItem) {
                EntityItem entityItem = (EntityItem) entity;
                ItemStack itemStack = getItemStack(entityItem);
                return getLightLevel(itemStack);
            } else if (entity instanceof EntityItemFrame) {
                EntityItemFrame entityItemFrame = (EntityItemFrame) entity;
                ItemStack itemStack = entityItemFrame.getDisplayedItem();
                return getLightLevel(itemStack);
            } else {
                return LightValueColor.LIGHT_VALUE_0.rgb16();
            }
        }
    }

    public static void removeLights(RenderGlobal renderGlobal) {
        synchronized (mapDynamicLights) {
            List<ColorDynamicLight> dynamicLights = mapDynamicLights.valueList();

            for (int i = 0; i < dynamicLights.size(); ++i) {
                ColorDynamicLight dynamicLight = dynamicLights.get(i);
                dynamicLight.updateLitChunks(renderGlobal);
            }

            dynamicLights.clear();
        }
    }

    public static void clear() {
        synchronized (mapDynamicLights) {
            mapDynamicLights.clear();
        }
    }

    public static int getCount() {
        synchronized (mapDynamicLights) {
            return mapDynamicLights.size();
        }
    }

    public static ItemStack getItemStack(EntityItem entityItem) {
        return entityItem.getDataWatcher().getWatchableObjectItemStack(10);
    }

    @RequiredArgsConstructor
    private static class DoubleBrightness {
        public final double r;
        public final double g;
        public final double b;
    }
}
