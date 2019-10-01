package com.manzo.slang.extensions

import android.text.InputFilter
import android.widget.EditText

/**
 * Created by Manolo D'Antonio on 29/08/2019
 */

/**
 * Set max characters keeping all previous filters, replacing previous max length
 * @receiver EditText
 * @param length Int
 */
fun EditText.setMaxLength(length: Int) {
    mutableListOf<InputFilter>(InputFilter.LengthFilter(length))
        .apply { addAll(filters.filterNot { it is InputFilter.LengthFilter }) }
        .toTypedArray()
        .let { filters = it }
}
