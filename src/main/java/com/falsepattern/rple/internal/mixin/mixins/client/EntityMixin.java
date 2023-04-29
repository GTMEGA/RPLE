/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.Utils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public double posX;

    @Shadow public double posZ;

    @Shadow public World worldObj;

    @Shadow @Final public AxisAlignedBB boundingBox;

    @Shadow public double posY;

    @Shadow public float yOffset;

    /**
     * @author FalsePattern
     * @reason Fix with colors
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float p_70070_1_)
    {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.posZ);

        if (this.worldObj.blockExists(i, 0, j))
        {
            double d0 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
            int k = MathHelper.floor_double(this.posY - (double)this.yOffset + d0);
            int cookie = this.worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0);
            long packed = Utils.cookieToPackedLong(cookie);
            return Utils.getBrightestPair(packed);
        }
        else
        {
            return 0;
        }
    }
}
