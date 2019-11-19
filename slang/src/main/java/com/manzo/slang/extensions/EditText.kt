package com.manzo.slang.extensions

import android.annotation.SuppressLint
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.EditText

/**
 * Created by Manolo D'Antonio on 29/08/2019
 */

/**
 * Set max characters keeping all previous InputFilters, only replacing previous max length
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
 *
 * This sets a focusChangeListener. If you also need one, use the [onFocusChange] interface instead
 * of setting another listener, otherwise this function will stop working.
 * @receiver EditText
 * @param interval Int
 * @param separator String
 * @param onFocusChange ((view: View, hasFocus: Boolean) -> Unit)?
 */
fun EditText.setTextSeparator(
    interval: Int,
    separator: String,
    onFocusChange: ((view: View, hasFocus: Boolean) -> Unit)? = null
) {

    onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
        onFocusChange?.invoke(view, hasFocus)

        var watcher: TextWatcher? = null
        if (hasFocus) {
            watcher = object : TextWatcher {
                var isCancelling: Boolean = false
                @SuppressLint("SetTextI18n")
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
                        val editedText = text + separator
                        setText(editedText)
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


/**
 * Alternative constructor for [setValidations]
 * @receiver EditText
 * @param regex Regex
 * @param error String
 * @param interval Long
 * @param validationCompletedListener Function2<[@kotlin.ParameterName] Boolean, [@kotlin.ParameterName] Pair<Regex, String>?, Unit>?
 */
fun EditText.setValidation(
    regex: Regex,
    error: String,
    interval: Long = 1200,
    validationCompletedListener: ((isComplete: Boolean, failedValidation: Pair<Regex, String>?) -> Unit)? = null
) = setValidations(listOf(Pair(regex, error)), interval, validationCompletedListener)


/**
 * Add a validation procedure of the user input.
 * After the user stops typing for [interval] milliseconds,
 * every regex in [validations] is run to check for matches.
 * The first not found match will stop validations and show [EditText.setError]. The error
 * message will be the string paired with the failed regex.
 * @receiver EditText
 * @param validations List<Pair<Regex, String>>
 * @param interval Long
 * @param validationCompletedListener Function2<[@kotlin.ParameterName] Boolean, [@kotlin.ParameterName] Pair<Regex, String>?, Unit>?
 */
fun EditText.setValidations(
    validations: List<Pair<Regex, String>>,
    interval: Long = 1200,
    validationCompletedListener: ((isComplete: Boolean, failedValidation: Pair<Regex, String>?) -> Unit)? = null
) {
    val editText = this
    val handler = Handler()
    val runnable = Runnable {
        validations.forEach {
            it.first.find(text.toString()) ?: run {
                editText.error = it.second
                validationCompletedListener?.invoke(true, it)
                return@forEach
            }

            if (it == validations.last()) {
                validationCompletedListener?.invoke(true, null)
            }
        }
    }


    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            validationCompletedListener?.invoke(false, null)
            handler.postDelayed(runnable, interval)
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            handler.removeCallbacks(runnable)
            editText.error = null
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            //
        }
    })
}

