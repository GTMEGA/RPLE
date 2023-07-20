/*
 * Copyright (c) 2023 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.falsepattern.rple.internal;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Tags {
    public static final String MOD_ID = "GRADLETOKEN_MODID";
    public static final String MOD_NAME = "GRADLETOKEN_MODNAME";
    public static final String VERSION = "GRADLETOKEN_VERSION";
    public static final String GROUP_NAME = "GRADLETOKEN_GROUPNAME";

    public static final String MINECRAFT_VERSION = "[1.7.10]";
    public static final String DEPENDENCIES = "required-after:falsepatternlib@[0.11,);" +
                                              "required-after:chunkapi@[0.2,);" +
                                              "required-after:lumina;" +
                                              "required-after:falsetweaks@[2.4,)";

    public static final String PROXY_GROUP_NAME = GROUP_NAME + ".proxy";
    public static final String CLIENT_PROXY_CLASS_NAME = PROXY_GROUP_NAME + ".ClientProxy";
    public static final String SERVER_PROXY_CLASS_NAME = PROXY_GROUP_NAME + ".ServerProxy";
}
