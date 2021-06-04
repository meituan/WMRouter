package com.kronos.plugin.base

class DefaultClassNameFilter : ClassNameFilter {

    override fun filter(className: String): Boolean {
        whiteList.forEach {
            if (className.contains(it)) {
                return true
            }
        }
        return false
    }

    companion object {
        val whiteList = mutableListOf<String>().apply {
            add("kotlin")
            add("org")
            add("androidx")
            add("android")
        }
    }
}