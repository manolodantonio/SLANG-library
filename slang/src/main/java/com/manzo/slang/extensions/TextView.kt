package com.manzo.slang.extensions

import android.widget.TextView

/**
 * Created by Manolo D'Antonio on 29/08/2019
 */

/**
 * Set styled text from an HTML string
 * @receiver TextView
 * @param html String
 */
fun TextView.setTextHtml(html: String) {
    setText(html.toHtmlSpanned(), TextView.BufferType.SPANNABLE)
}