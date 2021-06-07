package com.kronos.plugin.base.utils

import com.kronos.plugin.base.Log
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes.ACC_STATIC
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InvokeDynamicInsnNode
import org.objectweb.asm.tree.MethodNode

/**
 *
 *  @Author LiABao
 *  @Since 2021/1/13
 *
 */

fun ClassNode.lambdaHelper(isStatic: Boolean = false, block: (InvokeDynamicInsnNode) -> Boolean): MutableList<MethodNode> {
    val lambdaMethodNodes = mutableListOf<MethodNode>()
    methods?.forEach { method ->
        method?.instructions?.iterator()?.forEach {
            if (it is InvokeDynamicInsnNode) {
                if (block.invoke(it)) {
                    //   Log.info("dynamicName:${it.name} dynamicDesc:${it.desc}")
                    val args = it.bsmArgs
                    args.forEach { arg ->
                        if (arg is Handle) {
                            val methodNode = findMethodByNameAndDesc(arg.name, arg.desc)
                            methodNode?.apply {
                                val hasStatic = access and ACC_STATIC != 0
                                if (isStatic) {
                                    if (hasStatic) {
                                        lambdaMethodNodes.add(this)
                                    }
                                } else {
                                    if (!hasStatic) {
                                        lambdaMethodNodes.add(this)
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }
    lambdaMethodNodes.forEach {

        Log.info("lambdaName:${it.name} lambdaDesc:${it.desc} lambdaAccess:${it.access}")
    }
    return lambdaMethodNodes

}

fun ClassNode.findMethodByNameAndDesc(name: String, desc: String): MethodNode? {
    return methods?.firstOrNull {
        it.name == name && it.desc == desc
    }
}