package com.falsepattern.rple.internal.asm;

import com.falsepattern.lib.asm.IClassNodeTransformer;
import com.falsepattern.rple.internal.LogHelper;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.asm.util.MethodDecl;
import com.falsepattern.rple.internal.asm.util.Util;
import lombok.val;
import lombok.var;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.Map;

import static com.falsepattern.rple.internal.common.config.RPLEConfig.Debug.DEBUG_ASM_TRANSFORMER;

public final class RPLEBlockColorInjector implements IClassNodeTransformer {
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
    public String getName() {
        return "RPLEBlockColorInjector";
    }

    @Override
    public boolean shouldTransform(ClassNode cn, String transformedName, boolean obfuscated) {
        if (transformedName.startsWith(Tags.GROUP_NAME))
            return false;
        return isValidTarget(cn);
    }

    @Override
    public void transform(ClassNode cn, String transformedName, boolean obfuscated) {
        val methodCount = cn.methods.size();
        for (var i = 0; i < methodCount; i++) {
            var method = cn.methods.get(i);
            for (val mapping : MAPPINGS.entrySet()) {
                if (tryTransform(cn, method, mapping.getKey(), mapping.getValue())) {
                    if (LogHelper.shouldLogDebug(DEBUG_ASM_TRANSFORMER))
                        RPLETransformer.LOG.debug("[BlockLightHooks] Transformed {}.{}{}", transformedName, method.name, method.desc);
                    break;
                }
            }
        }
    }

    private static boolean tryTransform(ClassNode cn, MethodNode method, MethodDecl decl, String newName) {
        if (!decl.matches(method))
            return false;
        method.name = newName;

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
                        break;
                    }
                }
            }
        }
        return true;
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
