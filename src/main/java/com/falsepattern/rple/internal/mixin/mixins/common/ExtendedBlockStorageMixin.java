/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common;

import com.falsepattern.rple.internal.mixin.hook.ColoredLightingHooks;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ExtendedBlockStorage.class, priority = 1010)
public abstract class ExtendedBlockStorageMixin {
    /**
     * @author Ven
     * @reason Colorize
     */
    @Overwrite
    public int getExtSkylightValue(int subChunkPosX, int subChunkPosY, int subChunkPosZ) {
        return ColoredLightingHooks.getMaxSkyLightValue(thiz(), subChunkPosX, subChunkPosY, subChunkPosZ);
    }

    /**
     * @author Ven
     * @reason Colorize
     */
    @Overwrite
    public int getExtBlocklightValue(int subChunkPosX, int subChunkPosY, int subChunkPosZ) {
        return ColoredLightingHooks.getMaxBlockLightValue(thiz(), subChunkPosX, subChunkPosY, subChunkPosZ);
    }

    private ExtendedBlockStorage thiz() {
        return (ExtendedBlockStorage) (Object) this;
    }
}
