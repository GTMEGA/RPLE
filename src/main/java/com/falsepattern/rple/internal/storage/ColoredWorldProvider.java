package com.falsepattern.rple.internal.storage;

import com.falsepattern.lumina.api.ILumiWorld;
import com.falsepattern.lumina.api.ILumiWorldProvider;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.IdentityHashMap;

public class ColoredWorldProvider implements ILumiWorldProvider {
    private final ColoredLightChannel channel;
    public ColoredWorldProvider(ColoredLightChannel channel) {
        this.channel = channel;
    }
    @Override
    public ILumiWorld getWorld(World world) {
        return ((ColoredCarrierWorld)world).getColoredWorld(channel);
    }
}
