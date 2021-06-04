package com.sankuai.waimai.router.plugin.visitor

import com.sankuai.waimai.router.interfaces.Const
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class ClassFilterVisitor extends ClassVisitor {
    private Set<String> classItems
    private Set<String> deleteItems

    ClassFilterVisitor(ClassVisitor classVisitor, Set<String> classItems, Set<String> deleteItems) {
        super(Opcodes.ASM6, classVisitor)
        this.classItems = classItems
        this.deleteItems = deleteItems
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name == Const.INIT_METHOD && desc == "()V") {
            TryCatchMethodVisitor methodVisitor = new TryCatchMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions),
                    classItems, deleteItems)
            return methodVisitor
        }
        return super.visitMethod(access, name, desc, signature, exceptions)
    }

}