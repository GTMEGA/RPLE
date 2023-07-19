/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal;


import com.falsepattern.rple.internal.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import lombok.NoArgsConstructor;

import static com.falsepattern.rple.internal.Tags.*;

@Mod(modid = MOD_ID,
     version = VERSION,
     name = MOD_NAME,
     acceptedMinecraftVersions = MINECRAFT_VERSION,
     dependencies = DEPENDENCIES)
@NoArgsConstructor
public final class RightProperLightingEngine {
    @SidedProxy(clientSide = CLIENT_PROXY_CLASS_NAME, serverSide = SERVER_PROXY_CLASS_NAME)
    public static CommonProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        PROXY.preInit(evt);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evt) {
        PROXY.init(evt);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        PROXY.postInit(evt);
    }

    @Mod.EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent evt) {
        PROXY.serverAboutToStart(evt);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent evt) {
        PROXY.serverStarting(evt);
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent evt) {
        PROXY.serverStarted(evt);
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent evt) {
        PROXY.serverStopping(evt);
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent evt) {
        PROXY.serverStopped(evt);
    }
}
