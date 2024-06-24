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

package com.falsepattern.rple.internal.asm;

import com.falsepattern.lib.turboasm.ClassNodeHandle;
import com.falsepattern.lib.turboasm.TurboClassTransformer;
import com.falsepattern.rple.internal.common.util.LogHelper;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.asm.util.MethodDecl;
import com.falsepattern.rple.internal.asm.util.Util;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.Map;

import static com.falsepattern.rple.internal.common.config.RPLEConfig.Debug.DEBUG_ASM_TRANSFORMER;

public final class RPLEBlockColorInjector implements TurboClassTransformer {
    // @formatter:off
    private static final String NAME_LIGHT_VALUE_DEOBF     = "getLightValue";
    private static final String NAME_LIGHT_VALUE_OBF       = "func_149750_m";
    private static final String NAME_LIGHT_OPACITY_DEOBF   = "getLightOpacity";
    private static final String NAME_LIGHT_OPACITY_OBF     = "func_149717_k";
    private static final String DESC_LIGHT_RAW             = "()I";
    private static final String DESC_LIGHT_POSITIONAL      = "(Lnet/minecraft/world/IBlockAccess;III)I";

    private static final String NAME_LIGHT_VALUE_RENAMED   = "rple$renamed$getLightValue";
    private static final String NAME_LIGHT_OPACITY_RENAMED = "rple$renamed$getLightOpacity";

    private static final MethodDecl DECL_LIGHT_VALUE_RAW_OBF      = new MethodDecl(NAME_LIGHT_VALUE_OBF,     DESC_LIGHT_RAW       );
    private static final MethodDecl DECL_LIGHT_VALUE_RAW_DEOBF    = new MethodDecl(NAME_LIGHT_VALUE_DEOBF,   DESC_LIGHT_RAW       );
    private static final MethodDecl DECL_LIGHT_VALUE_POSITIONAL   = new MethodDecl(NAME_LIGHT_VALUE_DEOBF,   DESC_LIGHT_POSITIONAL);
    private static final MethodDecl DECL_LIGHT_OPACITY_RAW_OBF    = new MethodDecl(NAME_LIGHT_OPACITY_OBF,   DESC_LIGHT_RAW       );
    private static final MethodDecl DECL_LIGHT_OPACITY_RAW_DEOBF  = new MethodDecl(NAME_LIGHT_OPACITY_DEOBF, DESC_LIGHT_RAW       );
    private static final MethodDecl DECL_LIGHT_OPACITY_POSITIONAL = new MethodDecl(NAME_LIGHT_OPACITY_DEOBF, DESC_LIGHT_POSITIONAL);

    private static final MethodDecl[] POTENTIAL_CANDIDATES = {
            DECL_LIGHT_VALUE_RAW_OBF,
            DECL_LIGHT_VALUE_RAW_DEOBF,
            DECL_LIGHT_VALUE_POSITIONAL,
            DECL_LIGHT_OPACITY_RAW_OBF,
            DECL_LIGHT_OPACITY_RAW_DEOBF,
            DECL_LIGHT_OPACITY_POSITIONAL
    };
    private static final int POTENTIAL_CANDIDATE_COUNT = POTENTIAL_CANDIDATES.length;

    private static final Map<MethodDecl, String> MAPPINGS;
    static {
        MAPPINGS = new HashMap<>();
        MAPPINGS.put(DECL_LIGHT_VALUE_RAW_OBF,      NAME_LIGHT_VALUE_RENAMED  );
        MAPPINGS.put(DECL_LIGHT_VALUE_RAW_DEOBF,    NAME_LIGHT_VALUE_RENAMED  );
        MAPPINGS.put(DECL_LIGHT_VALUE_POSITIONAL,   NAME_LIGHT_VALUE_RENAMED  );
        MAPPINGS.put(DECL_LIGHT_OPACITY_RAW_OBF,    NAME_LIGHT_OPACITY_RENAMED);
        MAPPINGS.put(DECL_LIGHT_OPACITY_RAW_DEOBF,  NAME_LIGHT_OPACITY_RENAMED);
        MAPPINGS.put(DECL_LIGHT_OPACITY_POSITIONAL, NAME_LIGHT_OPACITY_RENAMED);
    }

    private static final String INTERNAL_BLOCK = "net/minecraft/block/Block";
    private static final Map<String, Boolean> BLOCK_SUBCLASS_MEMOIZATION = new HashMap<>(1024, 0.2F);
    static {
        BLOCK_SUBCLASS_MEMOIZATION.put(INTERNAL_BLOCK, true);
    }

    // @formatter:on

    @Override
    public String owner() {
        return Tags.MOD_NAME;
    }

    @Override
    public String name() {
        return "RPLEBlockColorInjector";
    }

    @Override
    public boolean shouldTransformClass(@NotNull String className, @NotNull ClassNodeHandle classNode) {
        return !className.startsWith(Tags.GROUP_NAME);
    }

    @Override
    public boolean transformClass(@NotNull String className, @NotNull ClassNodeHandle classNode) {
        val cn = classNode.getNode();
        if (cn == null)
            return false;
        if (!isValidTarget(cn))
            return false;

        boolean modified = false;
        val methodCount = cn.methods.size();
        for (var i = 0; i < methodCount; i++) {
            var method = cn.methods.get(i);
            for (val mapping : MAPPINGS.entrySet()) {
                if (tryTransform(cn, method, mapping.getKey(), mapping.getValue())) {
                    modified = true;
                    if (LogHelper.shouldLogDebug(DEBUG_ASM_TRANSFORMER))
                        RPLETransformer.LOG.debug("[BlockLightHooks] Transformed {}.{}{}", className, method.name, method.desc);
                    break;
                }
            }
        }
        return modified;
    }

    private static boolean tryTransform(ClassNode cn, MethodNode method, MethodDecl decl, String newName) {
        if (!decl.matches(method))
            return false;
        method.name = newName;

        boolean modified = false;
        val insts = method.instructions.iterator();
        while (insts.hasNext()) {
            val inst = insts.next();
            if (inst instanceof MethodInsnNode) {
                val insnNode = (MethodInsnNode) inst;
                if (!insnNode.owner.equals(cn.name) && !insnNode.owner.equals(cn.superName) && !isBlockSubclass(insnNode.owner))
                    continue;
                for (val mapping : MAPPINGS.entrySet()) {
                    if (mapping.getKey().matches(insnNode)) {
                        insnNode.name = mapping.getValue();
                        modified = true;
                        break;
                    }
                }
            }
        }
        return modified;
    }

    private static boolean isBlockSubclass(String className) {
        if (className == null)
            return false;

        val v = BLOCK_SUBCLASS_MEMOIZATION.get(className);
        if (v != null)
            return v;

        if (className.equals(INTERNAL_BLOCK)) {
            BLOCK_SUBCLASS_MEMOIZATION.put(className, true);
            return true;
        }

        val classBytes = Util.bytesFromInternalName(className);
        if (classBytes == null) {
            BLOCK_SUBCLASS_MEMOIZATION.put(className, false);
            return false;
        }

        return isBlockSubclass(new ClassReader(classBytes).getSuperName());
    }

    /**
     * Detect classes that can be patched based on their methods. This avoids unnecessary parsing of superclasses.
     */
    private static boolean isValidTarget(ClassNode cn) {
        val methodCount = cn.methods.size();
        for (var i = 0; i < methodCount; i++) {
            var method = cn.methods.get(i);
            for (int j = 0; j < POTENTIAL_CANDIDATE_COUNT; j++) {
                if (POTENTIAL_CANDIDATES[j].matches(method))
                    return isBlockSubclass(cn.superName);
            }
        }
        return false;
    }
}
