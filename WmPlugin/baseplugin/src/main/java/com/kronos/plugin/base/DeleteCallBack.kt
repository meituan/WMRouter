package com.kronos.plugin.base

interface DeleteCallBack {
    fun delete(className: String, classBytes: ByteArray)
}