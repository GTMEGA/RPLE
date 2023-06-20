/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.lumina;

import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.internal.common.helper.storage.ColoredCarrierChunk;
import com.falsepattern.rple.internal.common.helper.storage.ColoredCarrierWorld;
import com.falsepattern.rple.internal.common.helper.storage.ColoredLightChunk;
import lombok.val;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Chunk.class)
public abstract class ChunkMixin implements ColoredCarrierChunk {
    @Shadow public World worldObj;
    private ColoredLightChunk cRed;
    private ColoredLightChunk cGreen;
    private ColoredLightChunk cBlue;
    private boolean colorInit;
    @Override
    public ColoredLightChunk getColoredChunk(int colorChannel) {
        if (!colorInit) {
            initColoredChunk();
            colorInit = true;
        }
        switch (colorChannel) {
            case LightConstants.COLOR_CHANNEL_RED: return cRed;
            case LightConstants.COLOR_CHANNEL_GREEN: return cGreen;
            case LightConstants.COLOR_CHANNEL_BLUE: return cBlue;
            default: throw new IllegalArgumentException();
        }
    }


    private void initColoredChunk() {
        val carr = (ColoredCarrierWorld)worldObj;
        cRed = new ColoredLightChunk(carr.getColoredWorld(LightConstants.COLOR_CHANNEL_RED), (Chunk) (Object) this);
        cGreen = new ColoredLightChunk(carr.getColoredWorld(LightConstants.COLOR_CHANNEL_GREEN), (Chunk) (Object) this);
        cBlue = new ColoredLightChunk(carr.getColoredWorld(LightConstants.COLOR_CHANNEL_BLUE), (Chunk) (Object) this);
    }
}
