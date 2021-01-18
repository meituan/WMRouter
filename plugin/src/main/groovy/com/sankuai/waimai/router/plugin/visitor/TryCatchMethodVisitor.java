package com.sankuai.waimai.router.plugin.visitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashSet;
import java.util.Set;

public class TryCatchMethodVisitor extends MethodVisitor {
    private Set<String> deleteItems;
    private Set<String> addItems;

    public TryCatchMethodVisitor(MethodVisitor mv, Set<String> addItems, Set<String> deleteItems) {
        super(Opcodes.ASM5, mv);
        this.deleteItems = deleteItems;
        this.addItems = addItems;
        if (this.addItems == null) {
            this.addItems = new HashSet<>();
        }
        if (this.deleteItems == null) {
            this.deleteItems = new HashSet<>();
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        String className = owner + ".class";
        if (!deleteItems.contains(className)) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

    @Override
    public void visitCode() {
        super.visitCode();
        for (String input : addItems) {
            input = input.replace(".class", "");
            input = input.replace(".", "/");
            deleteItems.add(input + ".class");
            addTryCatchMethodInsn(Opcodes.INVOKESTATIC, input, "init", "()V", false);
        }
    }


    public void addTryCatchMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        mv.visitMethodInsn(opcode, owner, name, desc, itf);
    }
}
