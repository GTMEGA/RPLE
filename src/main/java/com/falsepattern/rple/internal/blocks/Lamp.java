/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.blocks;

import com.falsepattern.rple.api.ColoredBlock;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.client.render.IIconWrapper;
import com.falsepattern.rple.internal.client.render.LampRenderingHandler;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class Lamp extends Block implements ColoredBlock {
    private static final String GLOW_RESOURCE = Tags.MODID + ":glow";
    public static final int POWERED_BIT = 0b0010;
    public static final int INVERTED_BIT = 0b0001;
    private IIcon glowIcon;
    private IIcon poweredIcon;
    public Lamp() {
        super(Material.redstoneLight);
        setHardness(0.3F);
        setStepSound(soundTypeGlass);
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    public static boolean isGlowing(int meta) {
        return ((meta ^ (meta >>> 1)) & 1) == 1;
    }

    @Override
    public void registerBlockIcons(@NotNull IIconRegister register) {
        blockIcon = register.registerIcon(Tags.MODID + ":lamp/off/" + getTextureName());
        poweredIcon = register.registerIcon(Tags.MODID + ":lamp/on/" + getTextureName());
        glowIcon = new IIconWrapper(register.registerIcon(GLOW_RESOURCE));
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
        if (!world.isRemote) {
            val indirectlyPowered = world.isBlockIndirectlyGettingPowered(x, y, z);
            val meta = world.getBlockMetadata(x, y, z);
            val powered = (meta & POWERED_BIT) != 0;
            if (powered && !indirectlyPowered) {
                world.setBlockMetadataWithNotify(x, y, z, meta & ~POWERED_BIT, 3);
            } else if (!powered && indirectlyPowered) {
                world.setBlockMetadataWithNotify(x, y, z, meta | POWERED_BIT, 3);
            }
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
        return LampRenderingHandler.RENDER_ID;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public boolean canRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }
}