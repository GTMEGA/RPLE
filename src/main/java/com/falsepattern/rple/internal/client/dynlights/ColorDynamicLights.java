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

package com.falsepattern.rple.internal.client.dynlights;

import com.falsepattern.falsetweaks.api.dynlights.DynamicLightsDriver;
import com.falsepattern.falsetweaks.api.dynlights.FTDynamicLights;
import com.falsepattern.lib.util.MathUtil;
import com.falsepattern.rple.api.client.CookieMonster;
import com.falsepattern.rple.api.client.ClientColorHelper;
import com.falsepattern.rple.api.common.ServerColorHelper;
import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.color.DefaultColor;
import com.falsepattern.rple.api.common.color.LightValueColor;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
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

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ColorDynamicLights implements DynamicLightsDriver {
    public static final ColorDynamicLights INSTANCE = new ColorDynamicLights(false);
    private static final ColorDynamicLights FOR_WORLD = new ColorDynamicLights(true);
    private static final Int2ObjectMap<ColorDynamicLight> mapDynamicLights = new Int2ObjectArrayMap<>();
    private static final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private static long timeUpdateMs = 0L;
    private final boolean forWorld;
    private static final double MAX_DIST = 16.0;
    private static final double MAX_DIST_SQ = MAX_DIST * MAX_DIST;
    private static final int LIGHT_LEVEL_MAX = 15;

    private static ReentrantReadWriteLock.WriteLock busyWaitWriteLock() {
        val lock = rwLock.writeLock();
        while (!lock.tryLock()) {
            Thread.yield();
        }
        return lock;
    }

    private static ReentrantReadWriteLock.ReadLock busyWaitReadLock() {
        val lock = rwLock.readLock();
        while (!lock.tryLock()) {
            Thread.yield();
        }
        return lock;
    }

    private ColorDynamicLights(boolean forWorld) {
        this.forWorld = forWorld;
    }

    @Override
    public DynamicLightsDriver forWorldMesh() {
        return FOR_WORLD;
    }

    @Override
    public boolean enabled() {
        return FTDynamicLights.isDynamicLights();
    }

    @Override
    public void entityAdded(Entity entityIn, RenderGlobal renderGlobal) {
    }

    @Override
    public void entityRemoved(Entity entityIn, RenderGlobal renderGlobal) {
        val lock = busyWaitWriteLock();
        try {
            ColorDynamicLight dynamicLight = mapDynamicLights.remove(entityIn.getEntityId());
            if (dynamicLight != null) {
                dynamicLight.updateLitChunks(renderGlobal);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void update(RenderGlobal renderGlobal) {
        long timeNowMs = System.currentTimeMillis();
        if (timeNowMs < timeUpdateMs + 50L) {
            return;
        }
        timeUpdateMs = timeNowMs;
        val lock = busyWaitWriteLock();
        try {
            updateMapDynamicLights(renderGlobal);
            if (!mapDynamicLights.isEmpty()) {
                for (val dynamicLight: mapDynamicLights.values()) {
                    dynamicLight.update(renderGlobal);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void updateMapDynamicLights(RenderGlobal renderGlobal) {
        World world = renderGlobal.theWorld;
        if (world != null) {
            for (Entity entity : world.getLoadedEntityList()) {
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

    @Override
    public int getCombinedLight(int x, int y, int z, int combinedLight) {
        val lightPlayer = getLightLevel(x, y, z);
        return getCombinedLight(lightPlayer, combinedLight);
    }

    @Override
    public int getCombinedLight(Entity entity, int combinedLight) {
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

    private DoubleBrightness getLightLevel(int x, int y, int z) {
        val rve = Minecraft.getMinecraft().renderViewEntity;
        double lightLevelMaxR = 0.0;
        double lightLevelMaxG = 0.0;
        double lightLevelMaxB = 0.0;
        val lock = busyWaitReadLock();
        try {
            for (val dynamicLight: mapDynamicLights.values()) {
                if (!FTDynamicLights.isDynamicHandLight(forWorld) && dynamicLight.getEntity() == rve) {
                    continue;
                }
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
                    if (dynamicLight.isUnderwater()) {
                        dynamicR = MathUtil.clamp(dynamicR - 2, 0, LIGHT_LEVEL_MAX);
                        dynamicG = MathUtil.clamp(dynamicG - 2, 0, LIGHT_LEVEL_MAX);
                        dynamicB = MathUtil.clamp(dynamicB - 2, 0, LIGHT_LEVEL_MAX);
                        dx *= 2.0;
                        dy *= 2.0;
                        dz *= 2.0;
                    }
                    double lightR;
                    double lightG;
                    double lightB;

                    if (FTDynamicLights.isCircular()) {
                        double distSq = dx * dx + dy * dy + dz * dz;
                        if (distSq <= MAX_DIST_SQ) {
                            val dist = MathUtil.sqrt(distSq);
                            lightR = MathUtil.clamp(dynamicR - dist, 0, LIGHT_LEVEL_MAX);
                            lightG = MathUtil.clamp(dynamicG - dist, 0, LIGHT_LEVEL_MAX);
                            lightB = MathUtil.clamp(dynamicB - dist, 0, LIGHT_LEVEL_MAX);
                        } else {
                            lightR = lightG = lightB = 0;
                        }
                    } else {
                        dx = Math.abs(dx);
                        dy = Math.abs(dy);
                        dz = Math.abs(dz);
                        double dist = Math.max(dx - 0.25, 0) + Math.max(dy - 0.25, 0) + Math.max(dz - 0.25, 0);
                        lightR = MathUtil.clamp(dynamicR - dist, 0, LIGHT_LEVEL_MAX);
                        lightG = MathUtil.clamp(dynamicG - dist, 0, LIGHT_LEVEL_MAX);
                        lightB = MathUtil.clamp(dynamicB - dist, 0, LIGHT_LEVEL_MAX);
                    }

                    if (lightR > lightLevelMaxR) {
                        lightLevelMaxR = lightR;
                    }
                    if (lightG > lightLevelMaxG) {
                        lightLevelMaxG = lightG;
                    }
                    if (lightB > lightLevelMaxB) {
                        lightLevelMaxB = lightB;
                    }
                }
            }
        } finally {
            lock.unlock();
        }

        lightLevelMaxR = MathUtil.clamp(lightLevelMaxR, 0, LIGHT_LEVEL_MAX);
        lightLevelMaxG = MathUtil.clamp(lightLevelMaxG, 0, LIGHT_LEVEL_MAX);
        lightLevelMaxB = MathUtil.clamp(lightLevelMaxB, 0, LIGHT_LEVEL_MAX);
        return new DoubleBrightness(lightLevelMaxR, lightLevelMaxG, lightLevelMaxB);
    }

    // Note: Public for easier compat with https://github.com/Tesseract4D/OffhandLights, do not refactor.
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

    public short getLightLevel(Entity entity) {
        if (entity == Minecraft.getMinecraft().renderViewEntity && !FTDynamicLights.isDynamicHandLight(forWorld)) {
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

    @Override
    public void removeLights(RenderGlobal renderGlobal) {
        val lock = busyWaitWriteLock();
        try {
            for (val dynamicLight: mapDynamicLights.values()) {
                dynamicLight.updateLitChunks(renderGlobal);
            }

            mapDynamicLights.clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        val lock = busyWaitWriteLock();
        try {
            mapDynamicLights.clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getCount() {
        val lock = busyWaitReadLock();
        try {
            return mapDynamicLights.size();
        } finally {
            lock.unlock();
        }
    }

    private static ItemStack getItemStack(EntityItem entityItem) {
        return entityItem.getDataWatcher().getWatchableObjectItemStack(10);
    }

    @RequiredArgsConstructor
    private static class DoubleBrightness {
        public final double r;
        public final double g;
        public final double b;
    }
}
