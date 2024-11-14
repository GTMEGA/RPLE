package com.falsepattern.rple.internal;

import com.falsepattern.rple.internal.common.config.RPLEConfig;
import lombok.Getter;
import lombok.val;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class HardcoreDarkness {
    public static final HardcoreDarkness INSTANCE = new HardcoreDarkness();
    private boolean enabled;

    public boolean isEnabled() {
        return enabled && RPLEConfig.HD.MODE != RPLEConfig.HD.Mode.Disabled;
    }
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        val mc = Minecraft.getMinecraft();
        val player = mc.thePlayer;
        if (player != null) {
            val dim = player.dimension;
            enabled = true;
            for (val d: RPLEConfig.HD.DIMENSION_BLACKLIST) {
                if (dim == d) {
                    enabled = false;
                    break;
                }
            }
        } else {
            enabled = false;
        }
    }
}
