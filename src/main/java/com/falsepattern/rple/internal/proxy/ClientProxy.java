/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.proxy;

import com.falsepattern.rple.internal.client.render.LampRenderer;
import com.falsepattern.rple.internal.client.render.VertexConstants;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lombok.NoArgsConstructor;

import static com.falsepattern.rple.internal.client.lightmap.LightMapPipeline.lightMapPipeline;

@NoArgsConstructor
public final class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent evt) {
        super.preInit(evt);
        VertexConstants.initVertexConstants();
    }

    @Override
    public void init(FMLInitializationEvent evt) {
        super.init(evt);
        RenderingRegistry.registerBlockHandler(new LampRenderer());
        lightMapPipeline().registerLightMapProviders();
    }
}
