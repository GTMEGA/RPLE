package com.falsepattern.rple.internal.common.cache;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.lumina.api.storage.LumiBlockStorage;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.common.world.RPLEWorld;
import org.jetbrains.annotations.NotNull;

public interface RPLEBlockStorage extends LumiBlockStorage {
    @NotNull ColorChannel rple$channel();

    @Override
    @NotNull RPLEBlockStorageRoot lumi$root();

    @Override
    @NotNull RPLEWorld lumi$world();

    int rple$getChannelBrightnessForTessellator(int posX, int posY, int posZ, int minBlockLight);

    int rple$getChannelLightValueForRender(@NotNull LightType lightType, int posX, int posY, int posZ);
}
