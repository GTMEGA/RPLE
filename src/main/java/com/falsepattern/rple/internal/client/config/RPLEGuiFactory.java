package com.falsepattern.rple.internal.client.config;

import com.falsepattern.lib.config.ConfigException;
import com.falsepattern.lib.config.SimpleGuiConfig;
import com.falsepattern.lib.config.SimpleGuiFactory;
import com.falsepattern.rple.internal.common.config.RPLEConfig;
import lombok.NoArgsConstructor;
import net.minecraft.client.gui.GuiScreen;

import static com.falsepattern.rple.internal.Tags.MOD_ID;
import static com.falsepattern.rple.internal.Tags.MOD_NAME;


/**
 * Referenced from the Mod Container class.
 *
 * @see com.falsepattern.rple.internal.RightProperLightingEngine
 */
@NoArgsConstructor
@SuppressWarnings("unused")
public final class RPLEGuiFactory implements SimpleGuiFactory {
    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return RPLEGuiConfig.class;
    }

    public static final class RPLEGuiConfig extends SimpleGuiConfig {
        public RPLEGuiConfig(GuiScreen parent) throws ConfigException {
            super(parent, MOD_ID, MOD_NAME, RPLEConfig.class);
        }
    }
}
