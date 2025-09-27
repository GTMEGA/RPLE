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

package com.falsepattern.rple.internal.client.lightmap;

import com.falsepattern.rple.internal.Compat;
import com.falsepattern.rple.internal.common.config.RPLEConfig;
import lombok.val;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import net.minecraft.client.renderer.OpenGlHelper;

import static com.falsepattern.rple.internal.client.lightmap.LightMap.lightMap;

public class LightMapStateMachine {
    private static final int STATE_WAITING = 1;
    private static final int STATE_LIGHTMAP_BOUND = 2;
    private static final int STATE_TEXTURE_TOGGLED = 3;
    private static int currentState = STATE_WAITING;
    private static boolean isEnabled;
    //called by ASM
    public static void enableTex() {
        if (currentState != STATE_LIGHTMAP_BOUND) {
            currentState = STATE_WAITING;
            return;
        }
        isEnabled = true;
        currentState = STATE_TEXTURE_TOGGLED;
    }

    //called by ASM
    public static void disableTex() {
        if (currentState != STATE_LIGHTMAP_BOUND) {
            currentState = STATE_WAITING;
            return;
        }
        isEnabled = false;
        currentState = STATE_TEXTURE_TOGGLED;
    }

    public static void setActiveTexture(int texture) {
        if (RPLEConfig.Compat.FASTER_GL_STATE_TRACKING) {
            setActiveTextureFast(texture);
        } else {
            setActiveTextureSafe((texture));
        }
    }

    private static void setActiveTextureFast(int texture) {
        val shadersEnabled = Compat.shadersEnabled();
        switch (currentState) {
            case STATE_WAITING:
                if (texture == OpenGlHelper.lightmapTexUnit) {
                    currentState = STATE_LIGHTMAP_BOUND;
                    break;
                }
                break;
            case STATE_TEXTURE_TOGGLED:
                if (texture != OpenGlHelper.lightmapTexUnit) {
                    if (isEnabled || !shadersEnabled) {
                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                    } else {
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                    }

                    lightMap().toggleEnabled(isEnabled);
                }
                currentState = STATE_WAITING;
                break;
        }
        if (shadersEnabled)
            Compat.optiFineSetActiveTexture(texture);
    }

    private static void setActiveTextureSafe(int texture) {
        val shadersEnabled = Compat.shadersEnabled();
        val lastTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);

        if (lastTexture == OpenGlHelper.lightmapTexUnit && texture != OpenGlHelper.lightmapTexUnit) {
            val isTexture2DEnabled = GL11.glGetBoolean(GL11.GL_TEXTURE_2D);
            if (isTexture2DEnabled || !shadersEnabled) {
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            } else {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
            }

            lightMap().toggleEnabled(isTexture2DEnabled);
        }
        if (shadersEnabled)
            Compat.optiFineSetActiveTexture(texture);
    }
}
