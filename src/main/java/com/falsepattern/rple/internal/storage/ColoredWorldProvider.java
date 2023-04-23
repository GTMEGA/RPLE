package com.falsepattern.rple.internal.storage;

import com.falsepattern.lumina.api.ILumiWorld;
import com.falsepattern.lumina.api.ILumiWorldProvider;

import net.minecraft.world.World;

public class ColoredWorldProvider implements ILumiWorldProvider {
    private final int colorChannel;
    public ColoredWorldProvider(int colorChannel) {
        this.colorChannel = colorChannel;
    }
    @Override
    public ILumiWorld getWorld(World world) {
        return ((ColoredCarrierWorld)world).getColoredWorld(colorChannel);
    }
}
