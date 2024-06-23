/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.client.optifine;

import com.falsepattern.rple.api.common.ServerColorHelper;
import com.falsepattern.rple.api.common.color.LightValueColor;
import lombok.Getter;
import lombok.val;
import stubpackage.Config;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;


public class ColorDynamicLight {
    @Getter private Entity entity = null;
    @Getter private double offsetY = 0.0;
    @Getter private double lastPosX = -2.1474836E9F;
    @Getter private double lastPosY = -2.1474836E9F;
    @Getter private double lastPosZ = -2.1474836E9F;
    @Getter private short lastLightLevel = LightValueColor.LIGHT_VALUE_0.rgb16();
    @Getter private boolean underwater = false;
    private long timeCheckMs = 0L;

    public ColorDynamicLight(Entity entity) {
        this.entity = entity;
        this.offsetY = entity.getEyeHeight();
    }

    public void update(RenderGlobal renderGlobal) {
        if (Config.isDynamicLightsFast()) {
            long timeNowMs = System.currentTimeMillis();
            if (timeNowMs < this.timeCheckMs + 500L) {
                return;
            }

            this.timeCheckMs = timeNowMs;
        }

        double posX = this.entity.posX - 0.5;
        double posY = this.entity.posY - 0.5 + this.offsetY;
        double posZ = this.entity.posZ - 0.5;
        val lightLevel = ColorDynamicLights.getLightLevel(this.entity);
        double dx = posX - this.lastPosX;
        double dy = posY - this.lastPosY;
        double dz = posZ - this.lastPosZ;
        double delta = 0.1;
        if (!(Math.abs(dx) <= delta) || !(Math.abs(dy) <= delta) || !(Math.abs(
                dz) <= delta) || this.lastLightLevel != lightLevel) {
            this.underwater = false;
            World world = renderGlobal.theWorld;
            if (world != null) {
                Block block = world.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY),
                                             MathHelper.floor_double(posZ));
                this.underwater = block == Blocks.water;
            }

            if (lightLevel != LightValueColor.LIGHT_VALUE_0.rgb16()) {
                int distance = ServerColorHelper.maxColorComponent(lightLevel) + 1;
                renderGlobal.markBlockRangeForRenderUpdate((int) (posX - distance), (int) (posY - distance), (int) (posZ - distance),
                                                           (int) (posX + distance), (int) (posY + distance), (int) (posZ + distance));
            }

            this.updateLitChunks(renderGlobal);
            this.lastPosX = posX;
            this.lastPosY = posY;
            this.lastPosZ = posZ;
            this.lastLightLevel = lightLevel;
        }
    }

    public void updateLitChunks(RenderGlobal renderGlobal) {
        int distance = ServerColorHelper.maxColorComponent(lastLightLevel) + 1;
        renderGlobal.markBlockRangeForRenderUpdate((int) (lastPosX - distance), (int) (lastPosY - distance), (int) (lastPosZ - distance),
                                                   (int) (lastPosX + distance), (int) (lastPosY + distance), (int) (lastPosZ + distance));
    }

    public String toString() {
        return "Entity: " + this.entity + ", offsetY: " + this.offsetY;
    }
}
