package com.kronos.plugin.base

interface ClassNameFilter {
    fun filter(className: String): Boolean
}