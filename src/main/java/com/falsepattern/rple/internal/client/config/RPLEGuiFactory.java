/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
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

package com.falsepattern.rple.internal.client.config;

import com.falsepattern.lib.config.ConfigException;
import com.falsepattern.lib.config.ConfigurationManager;
import com.falsepattern.lib.config.SimpleGuiFactory;
import com.falsepattern.rple.internal.common.config.RPLEConfig;
import lombok.NoArgsConstructor;
import lombok.val;

import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

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

    public static final class RPLEGuiConfig extends GuiConfig {
        public RPLEGuiConfig(GuiScreen parent) throws ConfigException {
            super(parent, getConfigElements(), MOD_ID, false, false, MOD_NAME + "Configuration");
        }
        @SuppressWarnings({"rawtypes"})
        private static List<IConfigElement> getConfigElements() throws ConfigException {
            val result = new ArrayList<IConfigElement>();
            result.add(category("general", RPLEConfig.General.class));
            result.add(category("debug", RPLEConfig.Debug.class));
            result.add(category("compat", RPLEConfig.Compat.class));
            result.add(category("hd", RPLEConfig.HD.class));
            return result;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private static IConfigElement category(String name, Class<?> klass) throws ConfigException {
            return new DummyConfigElement.DummyCategoryElement(name,
                                                               "config.rple." + name + ".category",
                                                               ConfigurationManager.getConfigElementsMulti(klass));
        }
    }
}
