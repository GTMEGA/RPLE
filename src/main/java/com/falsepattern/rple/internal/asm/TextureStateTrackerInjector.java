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

package com.falsepattern.rple.internal.asm;

import lombok.val;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class TextureStateTrackerInjector implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.startsWith("com.falsepattern.rple.") ||
            transformedName.startsWith("org.lwjgl.") ||
            transformedName.startsWith("org.lwjglx."))
            return basicClass;

        val cn = new ClassNode();

        new ClassReader(basicClass).accept(cn, 0);

        boolean modified = false;
        for (val method: cn.methods) {
            val iter = method.instructions.iterator();
            while (iter.hasNext()) {
                val insn = iter.next();
                if (insn.getOpcode() != Opcodes.INVOKESTATIC || !(insn instanceof MethodInsnNode))
                    continue;
                val mInsn = (MethodInsnNode) insn;
                if ("org.lwjgl.opengl.GL11".equals(mInsn.owner) ||
                    "org.lwjglx.opengl.GL11".equals(mInsn.owner))
                    continue;
                val isEnable = "glEnable".equals(mInsn.name);
                val isDisable = "glDisable".equals(mInsn.name);
                if (!isEnable && !isDisable)
                    continue;

                val previous = mInsn.getPrevious();

                if (previous.getOpcode() != Opcodes.SIPUSH && !(previous instanceof IntInsnNode))
                    continue;

                val intInsn = (IntInsnNode) previous;
                if (intInsn.operand != GL11.GL_TEXTURE_2D)
                    continue;

                iter.previous();
                iter.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/falsepattern/rple/internal/client/lightmap/LightMapStateMachine", isEnable ? "enableTex" : "disableTex", "()V", false));
                iter.next();
                modified = true;
            }
        }
        if (modified) {
            final ClassWriter writer = new ClassWriter(0);
            cn.accept(writer);
            return writer.toByteArray();
        } else {
            return basicClass;
        }
    }
}
