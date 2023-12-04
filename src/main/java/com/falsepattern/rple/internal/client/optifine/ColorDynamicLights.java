/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.client.optifine;

import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.color.CustomColor;
import com.falsepattern.rple.api.common.color.DefaultColor;
import com.falsepattern.rple.api.common.color.LightValueColor;
import com.falsepattern.rple.api.common.color.RPLEColor;
import com.falsepattern.rple.internal.client.render.CookieMonster;
import com.falsepattern.rple.internal.client.render.TessellatorBrightnessHelper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import stubpackage.Config;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
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
                if (lightLevel.red() > 0 || lightLevel.green() > 0 || lightLevel.blue() > 0) {
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
        return getCombinedLight(new DoubleBrightness(lightPlayer.red(), lightPlayer.green(), lightPlayer.blue()),
                                combinedLight);
    }

    private static int getCombinedLight(DoubleBrightness lightPlayer, int combinedLight) {
        if (lightPlayer.r > 0 || lightPlayer.g > 0 || lightPlayer.b > 0) {
            long splitCombined = CookieMonster.cookieToPackedLong(combinedLight);
            int combinedR = TessellatorBrightnessHelper.getBrightnessRed(splitCombined);
            int combinedG = TessellatorBrightnessHelper.getBrightnessGreen(splitCombined);
            int combinedB = TessellatorBrightnessHelper.getBrightnessBlue(splitCombined);
            int combinedSr = TessellatorBrightnessHelper.getSkyLightChannelFromBrightnessRender(combinedR);
            int combinedSg = TessellatorBrightnessHelper.getSkyLightChannelFromBrightnessRender(combinedG);
            int combinedSb = TessellatorBrightnessHelper.getSkyLightChannelFromBrightnessRender(combinedB);
            int combinedBr = TessellatorBrightnessHelper.getBlockLightChannelFromBrightnessRender(combinedR);
            int combinedBg = TessellatorBrightnessHelper.getBlockLightChannelFromBrightnessRender(combinedG);
            int combinedBb = TessellatorBrightnessHelper.getBlockLightChannelFromBrightnessRender(combinedB);
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
            combinedLight = CookieMonster.packedLongToCookie(
                    TessellatorBrightnessHelper.packedBrightnessFromTessellatorBrightnessChannels(
                            TessellatorBrightnessHelper.channelsToBrightnessRender(combinedBr, combinedSr),
                            TessellatorBrightnessHelper.channelsToBrightnessRender(combinedBg, combinedSg),
                            TessellatorBrightnessHelper.channelsToBrightnessRender(combinedBb, combinedSb)));
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
                double dynamicR = dynamicLightLevel.red();
                double dynamicG = dynamicLightLevel.green();
                double dynamicB = dynamicLightLevel.blue();
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

    public static RPLEColor getLightLevel(ItemStack itemStack) {
        if (itemStack == null) {
            return LightValueColor.LIGHT_VALUE_0;
        } else {
            Item item = itemStack.getItem();
            if (item instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock) item;
                Block block = itemBlock.field_150939_a;
                if (block != null) {
                    return ((RPLEBlock) block).rple$getBrightnessColor(itemStack.getItemDamage());
                }
            }

            if (item == Items.lava_bucket) {
                return ((RPLEBlock) Blocks.lava).rple$getBrightnessColor();
            } else if (item == Items.blaze_rod || item == Items.blaze_powder) {
                return DefaultColor.DIM_ORANGE;
            } else if (item == Items.glowstone_dust) {
                return DefaultColor.DIM_YELLOW;
            } else if (item == Items.magma_cream) {
                return DefaultColor.DIM_RED;
            } else {
                return item == Items.nether_star ? ((RPLEBlock) Blocks.beacon).rple$getBrightnessColor()
                                                 : LightValueColor.LIGHT_VALUE_0;
            }
        }
    }

    public static RPLEColor getLightLevel(Entity entity) {
        if (entity == Minecraft.getMinecraft().renderViewEntity && !Config.isDynamicHandLight()) {
            return LightValueColor.LIGHT_VALUE_0;
        } else if (entity.isBurning()) {
            return ((RPLEBlock) Blocks.fire).rple$getBrightnessColor();
        } else if (entity instanceof EntityFireball) {
            return ((RPLEBlock) Blocks.fire).rple$getBrightnessColor();
        } else if (entity instanceof EntityTNTPrimed) {
            return ((RPLEBlock) Blocks.fire).rple$getBrightnessColor();
        } else if (entity instanceof EntityBlaze) {
            EntityBlaze entityBlaze = (EntityBlaze) entity;
            return entityBlaze.func_70845_n() ? ((RPLEBlock) Blocks.fire).rple$getBrightnessColor()
                                              : ((RPLEBlock) Blocks.lava).rple$getBrightnessColor();
        } else if (entity instanceof EntityMagmaCube) {
            EntityMagmaCube emc = (EntityMagmaCube) entity;
            return (double) emc.squishFactor > 0.6 ? ((RPLEBlock) Blocks.fire).rple$getBrightnessColor()
                                                   : ((RPLEBlock) Blocks.lava).rple$getBrightnessColor();
        } else {
            if (entity instanceof EntityCreeper) {
                EntityCreeper entityCreeper = (EntityCreeper) entity;
                if (entityCreeper.getCreeperState() > 0) {
                    return ((RPLEBlock) Blocks.fire).rple$getBrightnessColor();
                }
            }

            if (entity instanceof EntityLivingBase) {
                EntityLivingBase player = (EntityLivingBase) entity;
                ItemStack stackMain = player.getHeldItem();
                val levelMain = getLightLevel(stackMain);
                ItemStack stackHead = player.getEquipmentInSlot(4);
                val levelHead = getLightLevel(stackHead);
                return new CustomColor(Math.max(levelMain.red(), levelHead.red()),
                                       Math.max(levelMain.green(), levelHead.green()),
                                       Math.max(levelMain.blue(), levelHead.blue()));
            } else if (entity instanceof EntityItem) {
                EntityItem entityItem = (EntityItem) entity;
                ItemStack itemStack = getItemStack(entityItem);
                return getLightLevel(itemStack);
            } else {
                return LightValueColor.LIGHT_VALUE_0;
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
