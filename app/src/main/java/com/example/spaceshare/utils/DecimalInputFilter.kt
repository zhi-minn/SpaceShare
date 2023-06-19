package com.example.spaceshare.utils

import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.Spanned
import java.util.regex.Pattern

object DecimalInputFilter : InputFilter {
    private val decimalPattern = Pattern.compile("[0-9]+(\\.[0-9]{0,2})?")

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val newSource = source?.toString() ?: ""
        val newDest = dest?.let { SpannableStringBuilder(it) } ?: SpannableStringBuilder()
        newDest.insert(dstart, newSource)
        val matcher = decimalPattern.matcher(newDest)
        return if (!matcher.matches()) "" else null
    }
}