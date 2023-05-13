/*
 * Copyright (c) 2023 FalsePattern
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
import lombok.val;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class Lamp extends Block implements ColoredBlock {
    private static final String GLOW_RESOURCE = Tags.MODID + ":glow";
    public final boolean powered;
    private Block opposite;
    private IIcon glowIcon;
    public Lamp(boolean powered) {
        super(Material.redstoneLight);
        this.powered = powered;
        setHardness(0.3F);
        setStepSound(soundTypeGlass);
        if (!powered) {
            setCreativeTab(CreativeTabs.tabDecorations);
        }
    }

    public void setOpposite(Block block) {
        this.opposite = block;
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        super.registerBlockIcons(register);
        glowIcon = new IIconWrapper(register.registerIcon(GLOW_RESOURCE));
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
            if (powered && !indirectlyPowered) {
                world.scheduleBlockUpdate(x, y, z, this, 4);
            } else if (!powered && indirectlyPowered) {
                world.setBlock(x, y, z, opposite, 0, 3);
            }
        }
    }

    public void updateTick(World world, int x, int y, int z, Random rng) {
        if (!world.isRemote && powered && !world.isBlockIndirectlyGettingPowered(x, y, z)) {
            world.setBlock(x, y, z, opposite, 0, 3);
        }
    }

    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z) {
        return Item.getItemFromBlock(powered ? opposite : this);
    }

    protected ItemStack createStackedBlock(int meta) {
        return new ItemStack(powered ? opposite : this, 1, 0);
    }

    @Override
    public Item getItemDropped(int meta, Random rng, int fortune) {
        return Item.getItemFromBlock(powered ? opposite : this);
    }

    @Override
    public int getRenderType() {
        return LampRenderingHandler.RENDER_ID;
    }

    @Override
    public int getRenderBlockPass() {
        return 0;
    }

    @Override
    public boolean canRenderInPass(int pass) {
        return pass == 0 || (powered && pass == 1);
    }
}