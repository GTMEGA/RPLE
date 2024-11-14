/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
 */

package com.falsepattern.rple.internal;


import com.falsepattern.rple.internal.common.config.RPLEConfig;
import com.falsepattern.rple.internal.common.util.FastThreadLocal;
import com.falsepattern.rple.internal.proxy.CommonProxy;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.FMLLaunchHandler;

import lombok.NoArgsConstructor;

import static com.falsepattern.rple.internal.Tags.*;

@Mod(modid = MOD_ID,
     version = VERSION,
     name = MOD_NAME,
     acceptedMinecraftVersions = "[1.7.10]",
     guiFactory = GROUP_NAME + ".internal.client.config.RPLEGuiFactory",
     dependencies = "required-after:lumi@[1.0.2,);" +
             "after:falsetweaks@[3.4.0,);" + // Hard dep, but only on clientside!
             "required-after:falsepatternlib@[1.4.6,);")
@NoArgsConstructor
public final class RightProperLightingEngine {
    @SidedProxy(clientSide = GROUP_NAME + ".internal.proxy.ClientProxy",
            serverSide = GROUP_NAME + ".internal.proxy.ServerProxy")
    public static CommonProxy PROXY;

    static {
        RPLEConfig.poke();
        FastThreadLocal.setMainThread(Thread.currentThread());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        PROXY.preInit(evt);
        if (Loader.isModLoaded("hardcoredarkness")) {
            createSidedException("Remove the Hardcore Darkness mod and restart the game!\nRPLE has built-in hardcore darkness.");
        }
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


    private static void createSidedException(String text) {
        if (FMLLaunchHandler.side().isClient()) {
            throw ClientHelper.createException(text);
        } else {
            throw new Error(text);
        }
    }

    private static class ClientHelper {
        private static RuntimeException createException(String text) {
            return new MultiLineLoadingException(text);
        }
    }
}
