/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.rple;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.lumina.api.lighting.LumiLightingEngine;
import com.falsepattern.lumina.api.world.LumiWorld;
import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.common.world.RPLEWorld;
import com.falsepattern.rple.internal.common.world.RPLEWorldContainer;
import com.falsepattern.rple.internal.common.world.RPLEWorldRoot;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.falsepattern.lumina.api.init.LumiWorldBaseInit.LUMI_WORLD_BASE_INIT_METHOD_REFERENCE;
import static com.falsepattern.lumina.api.init.LumiWorldBaseInit.LUMI_WORLD_BASE_INIT_MIXIN_VALUE;
import static com.falsepattern.rple.api.color.ColorChannel.*;
import static com.falsepattern.rple.internal.mixin.plugin.MixinPlugin.POST_LUMINA_MIXIN_PRIORITY;

@Unique
@Mixin(value = World.class, priority = POST_LUMINA_MIXIN_PRIORITY)
public abstract class RPLEWorldRootImplMixin implements IBlockAccess, LumiWorld, RPLEWorldRoot {
    @Final
    @Shadow
    public Profiler theProfiler;

    @Nullable
    @SuppressWarnings("unused")
    private LumiLightingEngine lumi$lightingEngine;

    private RPLEWorld rple$redChannel;
    private RPLEWorld rple$greenChannel;
    private RPLEWorld rple$blueChannel;

    @Inject(method = LUMI_WORLD_BASE_INIT_METHOD_REFERENCE,
            at = @At("RETURN"),
            remap = false,
            require = 1)
    @Dynamic(LUMI_WORLD_BASE_INIT_MIXIN_VALUE)
    private void rpleWorldInit(CallbackInfo ci) {
        this.lumi$lightingEngine = null;

        this.rple$redChannel = new RPLEWorldContainer(RED_CHANNEL, thiz(), this, theProfiler);
        this.rple$greenChannel = new RPLEWorldContainer(GREEN_CHANNEL, thiz(), this, theProfiler);
        this.rple$blueChannel = new RPLEWorldContainer(BLUE_CHANNEL, thiz(), this, theProfiler);
    }

    @Override
    public RPLEWorld rple$world(ColorChannel channel) {
        switch (channel) {
            default:
            case RED_CHANNEL:
                return rple$redChannel;
            case GREEN_CHANNEL:
                return rple$greenChannel;
            case BLUE_CHANNEL:
                return rple$blueChannel;
        }
    }

    @Override
    public int rple$getChannelBrightnessForTessellator(ColorChannel channel,
                                                       int posX,
                                                       int posY,
                                                       int posZ,
                                                       int minBlockLight) {
        return rple$world(channel).rple$getChannelBrightnessForTessellator(posX, posY, posZ, minBlockLight);
    }

    @Override
    public int rple$getChannelLightValueForTessellator(ColorChannel channel,
                                                       LightType lightType,
                                                       int posX,
                                                       int posY,
                                                       int posZ) {
        return rple$world(channel).rple$getChannelLightValueForRender(lightType, posX, posY, posZ);
    }

    private World thiz() {
        return (World) (Object) this;
    }
}
