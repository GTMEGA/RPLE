/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.asm.util;

import lombok.Data;
import org.objectweb.asm.Opcodes;

@Data
public class HookTarget {
    public final String threadLocalName;
    public final MethodCall target;
    public final Local[] locals;

    public HookTarget(String threadLocalName, MethodCall target, Local... locals) {
        this.threadLocalName = threadLocalName;
        this.target = target;
        this.locals = locals;
    }

    public static Local aload(int index) {
        return new Local(Opcodes.ALOAD, index);
    }

    public static Local iload(int index) {
        return new Local(Opcodes.ILOAD, index);
    }

    @Data
    public static class Local {
        public final int opcode;
        public final int index;
    }
}
