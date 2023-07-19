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
        lightMapPipeline().registerLightMaps();
    }
}
