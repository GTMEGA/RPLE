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

package com.falsepattern.rple.internal.client.dynlights;

import com.falsepattern.falsetweaks.api.dynlights.FTDynamicLights;
import com.falsepattern.rple.api.common.ServerColorHelper;
import com.falsepattern.rple.api.common.color.LightValueColor;
import com.falsepattern.rple.internal.Compat;
import lombok.Getter;
import lombok.val;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;


public class ColorDynamicLight {
    @Getter private final Entity entity;
    @Getter private final double offsetY;
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
        val isHandLight = entity == Minecraft.getMinecraft().renderViewEntity && Compat.neodymiumActive() && !Compat.shadersEnabled();
        if (!isHandLight && FTDynamicLights.isDynamicLightsFast()) {
            long timeNowMs = System.currentTimeMillis();
            if (timeNowMs < this.timeCheckMs + 500L) {
                return;
            }

            this.timeCheckMs = timeNowMs;
        }

        double posX = this.entity.posX - 0.5;
        double posY = this.entity.posY - 0.5 + this.offsetY;
        double posZ = this.entity.posZ - 0.5;
        val lightLevel = ColorDynamicLights.INSTANCE.getLightLevel(this.entity);
        double dx = posX - this.lastPosX;
        double dy = posY - this.lastPosY;
        double dz = posZ - this.lastPosZ;
        double delta = 0.1;
        if (!(Math.abs(dx) <= delta) || !(Math.abs(dy) <= delta) || !(Math.abs(dz) <= delta) || this.lastLightLevel != lightLevel) {
            this.underwater = false;
            World world = renderGlobal.theWorld;
            if (world != null) {
                Block block = world.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ));
                this.underwater = block == Blocks.water;
            }

            if (!isHandLight) {
                if (lightLevel != LightValueColor.LIGHT_VALUE_0.rgb16()) {
                    int distance = ServerColorHelper.maxColorComponent(lightLevel) + 1;
                    renderGlobal.markBlockRangeForRenderUpdate((int) (posX - distance), (int) (posY - distance), (int) (posZ - distance),
                                                               (int) (posX + distance), (int) (posY + distance), (int) (posZ + distance));
                }

                this.updateLitChunks(renderGlobal);
            }
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
