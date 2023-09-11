package com.falsepattern.rple.internal.asm;

import com.falsepattern.lib.asm.IClassNodeTransformer;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.asm.util.HookTarget;
import com.falsepattern.rple.internal.asm.util.MethodCall;
import com.falsepattern.rple.internal.asm.util.MethodDecl;
import com.falsepattern.rple.internal.asm.util.Util;
import lombok.val;
import lombok.var;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import static com.falsepattern.rple.internal.asm.util.HookTarget.aload;
import static com.falsepattern.rple.internal.asm.util.HookTarget.iload;

public final class RPLEBlockColorInjector implements IClassNodeTransformer {
    // @formatter:off
    private static final String INTERNAL_OBJECT = Type.getInternalName(Object.class);

    private static final String INTERNAL_BLOCK = "net/minecraft/block/Block";

    private static final String INTERNAL_THREAD_LOCAL = "java/lang/ThreadLocal";
    private static final String DESC_THREAD_LOCAL     = "Ljava/lang/ThreadLocal;";
    private static final String NAME_THREAD_LOCAL_GET = "get";
    private static final String DESC_THREAD_LOCAL_GET = "()Ljava/lang/Object;";

    private static final String INTERNAL_BOOLEAN_BOXED = "java/lang/Boolean";
    private static final String DESC_BOOLEAN_BOXED     = "Ljava/lang/Boolean;";
    private static final String NAME_BOOLEAN_UNBOX     = "booleanValue";
    private static final String DESC_BOOLEAN_UNBOX     = "()Z";

    private static final String NAME_LIGHT_VALUE_DEOBF   = "getLightValue";
    private static final String NAME_LIGHT_VALUE_OBF     = "func_149750_m";
    private static final String NAME_LIGHT_OPACITY_DEOBF = "getLightOpacity";
    private static final String NAME_LIGHT_OPACITY_OBF   = "func_149717_k";
    private static final String DESC_LIGHT_RAW           = "()I";
    private static final String DESC_LIGHT_POSITIONAL    = "(Lnet/minecraft/world/IBlockAccess;III)I";

    private static final MethodDecl DECL_LIGHT_VALUE_RAW_OBF     = new MethodDecl(NAME_LIGHT_VALUE_OBF    , DESC_LIGHT_RAW       );
    private static final MethodDecl DECL_LIGHT_VALUE_RAW_DEOBF   = new MethodDecl(NAME_LIGHT_VALUE_DEOBF  , DESC_LIGHT_RAW       );
    private static final MethodDecl DECL_LIGHT_VALUE_POSITIONAL  = new MethodDecl(NAME_LIGHT_VALUE_DEOBF  , DESC_LIGHT_POSITIONAL);
    private static final MethodDecl DECL_LIGHT_OPACITY_RAW_OBF   = new MethodDecl(NAME_LIGHT_OPACITY_OBF  , DESC_LIGHT_RAW       );
    private static final MethodDecl DECL_LIGHT_OPACITY_RAW_DEOBF = new MethodDecl(NAME_LIGHT_OPACITY_DEOBF, DESC_LIGHT_RAW       );
    private static final MethodDecl DECL_LIGHT_OPACITY_POSITIONAL= new MethodDecl(NAME_LIGHT_OPACITY_DEOBF, DESC_LIGHT_POSITIONAL);

    private static final MethodDecl[] POTENTIAL_CANDIDATES = new MethodDecl[]{
            DECL_LIGHT_VALUE_RAW_OBF,
            DECL_LIGHT_VALUE_RAW_DEOBF,
            DECL_LIGHT_VALUE_POSITIONAL,
            DECL_LIGHT_OPACITY_RAW_OBF,
            DECL_LIGHT_OPACITY_RAW_DEOBF,
            DECL_LIGHT_OPACITY_POSITIONAL
    };

    private static final Map<MethodDecl, HookTarget> MAPPINGS = new HashMap<>();

    static {
        val NAME_RPLE_PASS_LIGHT_VALUE   = "rple$passInternalLightValue";
        val NAME_RPLE_PASS_LIGHT_OPACITY = "rple$passInternalLightOpacity";

        val INTERNAL_LIGHTING_HOOKS    = Tags.GROUP_NAME.replace('.', '/') + "/internal/mixin/hook/ColoredLightingHooks";
        val NAME_RPLE_LIGHT_VALUE      = "getLightValue";
        val NAME_RPLE_LIGHT_OPACITY    = "getLightOpacity";
        val DESC_RPLE_LIGHT_RAW        = "(Lnet/minecraft/block/Block;)I";
        val DESC_RPLE_LIGHT_POSITIONAL = "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/block/Block;III)I";

        val RPLE_LIGHT_VALUE_RAW          = new MethodCall(Opcodes.INVOKESTATIC, INTERNAL_LIGHTING_HOOKS, NAME_RPLE_LIGHT_VALUE  , DESC_RPLE_LIGHT_RAW       , false);
        val RPLE_LIGHT_VALUE_POSITIONAL   = new MethodCall(Opcodes.INVOKESTATIC, INTERNAL_LIGHTING_HOOKS, NAME_RPLE_LIGHT_VALUE  , DESC_RPLE_LIGHT_POSITIONAL, false);
        val RPLE_LIGHT_OPACITY_RAW        = new MethodCall(Opcodes.INVOKESTATIC, INTERNAL_LIGHTING_HOOKS, NAME_RPLE_LIGHT_OPACITY, DESC_RPLE_LIGHT_RAW       , false);
        val RPLE_LIGHT_OPACITY_POSITIONAL = new MethodCall(Opcodes.INVOKESTATIC, INTERNAL_LIGHTING_HOOKS, NAME_RPLE_LIGHT_OPACITY, DESC_RPLE_LIGHT_POSITIONAL, false);

        val LOCALS_RAW        = new HookTarget.Local[]{aload(0)};
        val LOCALS_POSITIONAL = new HookTarget.Local[]{aload(1), aload(0), iload(2), iload(3), iload(4)};

        val TARGET_LIGHT_VALUE_RAW          = new HookTarget(NAME_RPLE_PASS_LIGHT_VALUE  , RPLE_LIGHT_VALUE_RAW         , LOCALS_RAW       );
        val TARGET_LIGHT_VALUE_POSITIONAL   = new HookTarget(NAME_RPLE_PASS_LIGHT_VALUE  , RPLE_LIGHT_VALUE_POSITIONAL  , LOCALS_POSITIONAL);
        val TARGET_LIGHT_OPACITY_RAW        = new HookTarget(NAME_RPLE_PASS_LIGHT_OPACITY, RPLE_LIGHT_OPACITY_RAW       , LOCALS_RAW       );
        val TARGET_LIGHT_OPACITY_POSITIONAL = new HookTarget(NAME_RPLE_PASS_LIGHT_OPACITY, RPLE_LIGHT_OPACITY_POSITIONAL, LOCALS_POSITIONAL);

        MAPPINGS.put(DECL_LIGHT_VALUE_RAW_OBF     , TARGET_LIGHT_VALUE_RAW         );
        MAPPINGS.put(DECL_LIGHT_VALUE_RAW_DEOBF   , TARGET_LIGHT_VALUE_RAW         );
        MAPPINGS.put(DECL_LIGHT_VALUE_POSITIONAL  , TARGET_LIGHT_VALUE_POSITIONAL  );
        MAPPINGS.put(DECL_LIGHT_OPACITY_RAW_OBF   , TARGET_LIGHT_OPACITY_RAW       );
        MAPPINGS.put(DECL_LIGHT_OPACITY_RAW_DEOBF , TARGET_LIGHT_OPACITY_RAW       );
        MAPPINGS.put(DECL_LIGHT_OPACITY_POSITIONAL, TARGET_LIGHT_OPACITY_POSITIONAL);

    }

    // @formatter:on

    @Override
    public String getName() {
        return "RPLEBlockColorInjector";
    }

    @Override
    public boolean shouldTransform(ClassNode cn, String transformedName, boolean obfuscated) {
        if (transformedName.startsWith(Tags.GROUP_NAME)) {
            return false;
        }
        return isValidTarget(cn);
    }

    @Override
    public void transform(ClassNode cn, String transformedName, boolean obfuscated) {
        for (val method: cn.methods) {
            for (val mapping: MAPPINGS.entrySet()) {
                if (tryTransform(method, mapping.getKey(), mapping.getValue())) {
                    RPLETransformer.LOG.info("[BlockLightHooks] Transformed {}.{}{}", transformedName, method.name, method.desc);
                    break;
                }
            }
        }
    }

    private static boolean tryTransform(MethodNode method, MethodDecl decl, HookTarget target) {
        if (!decl.matches(method)) {
            return false;
        }
        val iter = method.instructions.iterator();
        addThreadLocalFetch(iter, target.threadLocalName);
        val jump = new LabelNode();
        iter.add(new JumpInsnNode(Opcodes.IFNE, jump));
        for (val local: target.locals) {
            iter.add(new VarInsnNode(local.opcode, local.index));
        }
        iter.add(target.target.asInsn());
        iter.add(new InsnNode(Opcodes.IRETURN));
        iter.add(jump);
        return true;
    }

    private static void addThreadLocalFetch(ListIterator<AbstractInsnNode> iter, String threadLocalName) {
        iter.add(new VarInsnNode(Opcodes.ALOAD, 0));
        iter.add(new FieldInsnNode(Opcodes.GETFIELD, INTERNAL_BLOCK, threadLocalName, DESC_THREAD_LOCAL));
        iter.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, INTERNAL_THREAD_LOCAL, NAME_THREAD_LOCAL_GET, DESC_THREAD_LOCAL_GET, false));
        iter.add(new TypeInsnNode(Opcodes.CHECKCAST, INTERNAL_BOOLEAN_BOXED));
        iter.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, INTERNAL_BOOLEAN_BOXED, NAME_BOOLEAN_UNBOX, DESC_BOOLEAN_UNBOX, false));
    }



    private static boolean isValidTarget(ClassNode node) {
        //Detect classes that can be patched based on their methods. This avoids unnecessary parsing of superclasses.
        boolean anyMatched = false;
        outer:
        for (val method: node.methods) {
            for (val candidate: POTENTIAL_CANDIDATES) {
                if (candidate.matches(method)) {
                    anyMatched = true;
                    break outer;
                }
            }
        }
        if (!anyMatched) {
            return false;
        }

        //Make sure the class is a subclass of Block
        var superClass = node.superName;
        while (!INTERNAL_OBJECT.equals(superClass)) {
            if (superClass.equals(INTERNAL_BLOCK))
                return true;
            val classBytes = Util.bytesFromInternalName(superClass);
            if (classBytes == null)
                return false;
            val cr = new ClassReader(classBytes);
            val superNode = new ClassNode();
            cr.accept(superNode, 0);
            superClass = superNode.superName;
        }
        return false;
    }
}
