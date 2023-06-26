/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.lumina;

import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.common.storage.ColoredCarrierChunk;
import com.falsepattern.rple.internal.common.storage.ColoredCarrierWorld;
import com.falsepattern.rple.internal.common.storage.ColoredLightChunk;
import lombok.val;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static com.falsepattern.rple.api.color.ColorChannel.*;

@Mixin(Chunk.class)
public abstract class ChunkMixin implements ColoredCarrierChunk {
    @Shadow
    public World worldObj;

    @Nullable
    private ColoredLightChunk cRed = null;
    @Nullable
    private ColoredLightChunk cGreen = null;
    @Nullable
    private ColoredLightChunk cBlue = null;

    private boolean colorInit = false;

    @Override
    public ColoredLightChunk getColoredChunk(ColorChannel channel) {
        if (!colorInit) {
            initColoredChunk();
            colorInit = true;
        }

        switch (channel) {
            default:
            case RED_CHANNEL:
                return cRed;
            case GREEN_CHANNEL:
                return cGreen;
            case BLUE_CHANNEL:
                return cBlue;
        }
    }

    private void initColoredChunk() {
        val carrierWorld = (ColoredCarrierWorld) worldObj;

        this.cRed = new ColoredLightChunk(carrierWorld.coloredWorld(RED_CHANNEL), thiz());
        this.cGreen = new ColoredLightChunk(carrierWorld.coloredWorld(GREEN_CHANNEL), thiz());
        this.cBlue = new ColoredLightChunk(carrierWorld.coloredWorld(BLUE_CHANNEL), thiz());
    }

    private Chunk thiz() {
        return (Chunk) (Object) this;
    }
}
