package com.kronos.plugin.base.utils

import java.io.File

/**
 * @Author LiABao
 * @Since 2021/1/4
 */

fun File.filterTest(nameReg: String): Array<File>? {
    return listFiles { p0 -> p0?.name == nameReg }
}

fun File.deleteAll() {
    delFolder(path)
}


private fun delAllFile(path: String): Boolean {
    var flag = false
    val file = File(path)
    if (!file.exists()) {
        return flag
    }
    if (!file.isDirectory) {
        return flag
    }
    val tempList = file.list()
    var temp: File? = null
    for (i in tempList.indices) {
        temp = if (path.endsWith(File.separator)) {
            File(path + tempList[i])
        } else {
            File(path + File.separator + tempList[i])
        }
        if (temp.isFile) {
            temp.delete()
        }
        if (temp.isDirectory) {
            delAllFile(path + "/" + tempList[i]) // 先删除文件夹里面的文件
            delFolder(path + "/" + tempList[i]) // 再删除空文件夹
            flag = true
        }
    }
    return flag
}


// 删除文件夹
// param folderPath 文件夹完整绝对路径
private fun delFolder(folderPath: String) {
    try {
        delAllFile(folderPath) // 删除完里面所有内容
        val myFilePath = File(folderPath)
        myFilePath.delete() // 删除空文件夹
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
