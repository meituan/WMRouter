package com.sankuai.waimai.router.plugin.visitor;

import com.sankuai.waimai.router.plugin.Const;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Set;

public class ClassFilterVisitor extends ClassVisitor {

    private Set<String> classItems;
    private Set<String> deleteItems;

    public ClassFilterVisitor(ClassVisitor classVisitor, Set<String> classItems, Set<String> deleteItems) {
        super(Opcodes.ASM6, classVisitor);
        this.classItems = classItems;
        this.deleteItems = deleteItems;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.equals(Const.INIT_METHOD) && desc.equals("()V")) {
            return new TryCatchMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions),
                    classItems, deleteItems);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

}