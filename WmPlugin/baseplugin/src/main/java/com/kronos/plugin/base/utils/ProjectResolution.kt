package com.kronos.plugin.base.utils

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.gradle.internal.VariantManager
import com.android.build.gradle.internal.scope.VariantScope
import com.android.utils.appendCapitalized
import com.google.common.base.CaseFormat
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.stream.Collectors

/**
 * Created by YangJing on 2020/01/07 .
 * Email: yangjing.yeoh@bytedance.com
 */
/*
fun getVariantManager(project: Project): VariantManager {
    val appPlugin: Plugin<Any>? = when {
        // AGP4.0.0-alpha07: move all methods to com.android.internal.application
        project.plugins.hasPlugin("com.android.internal.application") -> {
            project.plugins.getPlugin("com.android.internal.application")
        }
        project.plugins.hasPlugin("com.android.application") -> {
            project.plugins.getPlugin("com.android.application")
        }
        else -> {
            throw GradleException("Unexpected AppPlugin")
        }
    }
    return getVariantManagerFromAppPlugin(appPlugin) ?: throw GradleException("get VariantManager failed")
}

private fun getVariantManagerFromAppPlugin(appPlugin: Any?): VariantManager? {
    return if (appPlugin == null) return null else try {
        for (method in appPlugin::class.java.methods) {
            if (method.name == "getVariantManager") {
                return method.invoke(appPlugin) as VariantManager?
            }
        }
        for (method in appPlugin::class.java.declaredMethods) {
            if (method.name == "getVariantManager") {
                return method.invoke(appPlugin) as VariantManager?
            }
        }
        return null
    } catch (e: Exception) {
        null
    }
}

fun Transform.getTaskNamePrefix(): String {
    val sb = StringBuilder(100)
    sb.append("transform")
    sb.append(inputTypes.stream().map { inputType: QualifiedContent.ContentType ->
        CaseFormat.UPPER_UNDERSCORE.to(
                CaseFormat.UPPER_CAMEL, inputType.name()
        )
    }.sorted().collect(Collectors.joining("And")))
    sb.append("With")
    sb.appendCapitalized(name)
    sb.append("For")
    return sb.toString()
}


fun VariantManager.filterTest(): List<VariantScope> {
    return variantScopes.filter {
        !it.type.isForTesting
    }
}*/
