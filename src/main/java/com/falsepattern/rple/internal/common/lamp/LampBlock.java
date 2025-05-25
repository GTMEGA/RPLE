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

package com.falsepattern.rple.internal.common.lamp;

import com.falsepattern.rple.api.common.ServerColorHelper;
import com.falsepattern.rple.api.common.block.RPLECustomBlockBrightness;
import com.falsepattern.rple.api.common.color.DefaultColor;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.client.lamp.LampRenderer;
import lombok.Getter;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.falsepattern.lib.util.RenderUtil.wrapAsClampedIcon;

public abstract class LampBlock extends Block implements RPLECustomBlockBrightness {
    private static final String GLOW_RESOURCE = Tags.MOD_ID + ":glow";
    public static final int POWERED_BIT = 0b0010;
    public static final int INVERTED_BIT = 0b0001;

    @Getter
    private final short color;

    private IIcon glowIcon = null;
    private IIcon poweredIcon = null;

    public LampBlock(int r, int g, int b) {
        super(Material.redstoneLight);
        //RGB range checks
        if (r < ServerColorHelper.COLOR_MIN || r > ServerColorHelper.COLOR_MAX)
            throw new IllegalArgumentException("Red value must be between " + ServerColorHelper.COLOR_MIN + " and " + ServerColorHelper.COLOR_MAX);
        if (g < ServerColorHelper.COLOR_MIN || g > ServerColorHelper.COLOR_MAX)
            throw new IllegalArgumentException("Green value must be between " + ServerColorHelper.COLOR_MIN + " and " + ServerColorHelper.COLOR_MAX);
        if (b < ServerColorHelper.COLOR_MIN || b > ServerColorHelper.COLOR_MAX)
            throw new IllegalArgumentException("Blue value must be between " + ServerColorHelper.COLOR_MIN + " and " + ServerColorHelper.COLOR_MAX);
        color = ServerColorHelper.RGB16FromRGBChannel4Bit(r, g, b);
        setHardness(0.3F);
        setStepSound(soundTypeGlass);
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    public static boolean isGlowing(int meta) {
        return ((meta ^ (meta >>> 1)) & 1) == 1;
    }

    protected abstract String getOffTextureName();

    protected abstract String getOnTextureName();

    /**
     * Override getOffTextureName() and getOnTextureName() instead
     */
    @Override
    public void registerBlockIcons(@NotNull IIconRegister register) {
        blockIcon = register.registerIcon(getOffTextureName());
        poweredIcon = register.registerIcon(getOnTextureName());
        glowIcon = wrapAsClampedIcon(register.registerIcon(GLOW_RESOURCE));
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return isGlowing(meta) ? poweredIcon : blockIcon;
    }

    public IIcon getGlowIcon() {
        return glowIcon;
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World world, int x, int y, int z) {
        onBlockUpdate(world, x, y, z);
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        onBlockUpdate(world, x, y, z);
    }

    private void onBlockUpdate(World world, int x, int y, int z) {
        if (world.isRemote)
            return;

        val indirectlyPowered = world.isBlockIndirectlyGettingPowered(x, y, z);
        val meta = world.getBlockMetadata(x, y, z);
        val powered = (meta & POWERED_BIT) != 0;

        if (powered && !indirectlyPowered) {
            world.setBlockMetadataWithNotify(x, y, z, meta & ~POWERED_BIT, 3);
        } else if (!powered && indirectlyPowered) {
            world.setBlockMetadataWithNotify(x, y, z, meta | POWERED_BIT, 3);
        }
    }

    protected ItemStack createStackedBlock(int meta) {
        meta &= ~POWERED_BIT;
        return new ItemStack(this, 1, meta);
    }

    @Override
    public int damageDropped(int meta) {
        return meta & ~POWERED_BIT;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, INVERTED_BIT));
    }

    @Override
    public int getRenderType() {
        return LampRenderer.RENDER_ID;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public boolean canRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }

    @Override
    public short rple$getCustomBrightnessColor() {
        return DefaultColor.BLACK.rgb16();
    }

    protected static boolean shouldGlow(int meta) {
        val powered = (meta & POWERED_BIT) != 0;
        val inverted = (meta & INVERTED_BIT) != 0;
        return powered != inverted;
    }

    @Override
    public short rple$getCustomBrightnessColor(int blockMeta) {
        return shouldGlow(blockMeta) ? color : DefaultColor.BLACK.rgb16();
    }

    @Override
    public short rple$getCustomBrightnessColor(@NotNull IBlockAccess world, int blockMeta, int posX, int posY, int posZ) {
        return shouldGlow(blockMeta) ? color : DefaultColor.BLACK.rgb16();
    }
}
