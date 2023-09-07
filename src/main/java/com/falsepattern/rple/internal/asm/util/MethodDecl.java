/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.asm.util;

import lombok.Data;
import org.objectweb.asm.tree.MethodNode;

@Data
public class MethodDecl {
    public final String name;
    public final String desc;

    public boolean matches(MethodNode node) {
        return node.name.equals(name) && node.desc.equals(desc);
    }
}
