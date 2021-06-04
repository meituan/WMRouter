package com.kronos.plugin.base


object Log {
    @JvmStatic
    fun info(msg: Any) {
        try {
            println((String.format("{%s}", msg.toString())))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}