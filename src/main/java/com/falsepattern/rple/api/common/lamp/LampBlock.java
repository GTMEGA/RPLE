/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.api.common.lamp;

import com.falsepattern.rple.api.common.RPLEColorUtil;
import com.falsepattern.rple.api.common.block.RPLEBlockBrightnessColorProvider;
import com.falsepattern.rple.api.common.color.CustomColor;
import com.falsepattern.rple.api.common.color.DefaultColor;
import com.falsepattern.rple.api.common.color.RPLEColor;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.api.client.render.LampRenderer;
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

public abstract class LampBlock extends Block implements RPLEBlockBrightnessColorProvider {
    private static final String GLOW_RESOURCE = Tags.MOD_ID + ":glow";
    public static final int POWERED_BIT = 0b0010;
    public static final int INVERTED_BIT = 0b0001;

    @Getter
    private final RPLEColor color;

    private IIcon glowIcon = null;
    private IIcon poweredIcon = null;

    public LampBlock(int r, int g, int b) {
        super(Material.redstoneLight);
        //RGB range checks
        if (r < RPLEColorUtil.COLOR_MIN || r > RPLEColorUtil.COLOR_MAX)
            throw new IllegalArgumentException("Red value must be between " + RPLEColorUtil.COLOR_MIN + " and " + RPLEColorUtil.COLOR_MAX);
        if (g < RPLEColorUtil.COLOR_MIN || g > RPLEColorUtil.COLOR_MAX)
            throw new IllegalArgumentException("Green value must be between " + RPLEColorUtil.COLOR_MIN + " and " + RPLEColorUtil.COLOR_MAX);
        if (b < RPLEColorUtil.COLOR_MIN || b > RPLEColorUtil.COLOR_MAX)
            throw new IllegalArgumentException("Blue value must be between " + RPLEColorUtil.COLOR_MIN + " and " + RPLEColorUtil.COLOR_MAX);
        color = new CustomColor(r, g, b);
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
    public @NotNull RPLEColor rple$getCustomBrightnessColor() {
        return DefaultColor.BLACK;
    }

    protected static boolean shouldGlow(int meta) {
        val powered = (meta & POWERED_BIT) != 0;
        val inverted = (meta & INVERTED_BIT) != 0;
        return powered != inverted;
    }

    @Override
    public @NotNull RPLEColor rple$getCustomBrightnessColor(int blockMeta) {
        return shouldGlow(blockMeta) ? color : DefaultColor.BLACK;
    }

    @Override
    public @NotNull RPLEColor rple$getCustomBrightnessColor(@NotNull IBlockAccess world, int blockMeta, int posX, int posY, int posZ) {
        return shouldGlow(blockMeta) ? color : DefaultColor.BLACK;
    }
}
