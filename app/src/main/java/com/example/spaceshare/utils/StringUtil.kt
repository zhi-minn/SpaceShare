package com.example.spaceshare.utils

object StringUtil {

    fun String.isLettersOnly(): Boolean {
        val len = this.length
        for (i in 0 until len) {
            if (!this[i].isLetter()) {
                return false
            }
        }
        return true
    }
}