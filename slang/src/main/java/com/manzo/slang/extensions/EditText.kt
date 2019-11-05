package com.manzo.slang.extensions

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
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

/**
 * Will format the text while being inserted, adding [separator] every [interval] of characters
 * @receiver EditText
 * @param interval Int
 * @param separator String
 */
fun EditText.setTextSeparator(interval: Int, separator: String) {
    setOnFocusChangeListener { view, hasFocus ->
        var watcher: TextWatcher? = null
        if (hasFocus) {
            watcher = object : TextWatcher {
                var isCancelling: Boolean = false
                override fun afterTextChanged(editable: Editable) {
                    if (editable.isEmpty()) return

                    val text = editable.toString()
                    if (isCancelling && text.length > 1) {
                        removeTextChangedListener(this)
                        setText(text.removeRange(text.length - 2, text.length - 1))
                        setSelection(this@setTextSeparator.text.length)
                        addTextChangedListener(this)
                        return
                    }
                    // here we add separator
                    val lastChar = text.last().toString()
                    if (lastChar == separator) return

                    val cleanText = text.remove(separator)
                    if (cleanText.length % interval == 0) {
                        removeTextChangedListener(this)
                        setText(text + separator)
                        setSelection(this@setTextSeparator.text.length)
                        addTextChangedListener(this)
                    }
                }

                override fun beforeTextChanged(
                    chars: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                    isCancelling = when {
                        chars.isEmpty() -> false
                        // after != 0 means char is being added
                        after != 0 -> false
                        // check if separator is being cancelled
                        chars.last().toString() != separator -> false
                        // result true only if last character - edit not considered
                        else -> start == chars.length - 1
                    }
                }

                override fun onTextChanged(
                    chars: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    //
                }
            }
            addTextChangedListener(watcher)
        } else removeTextChangedListener(watcher)
    }

}

