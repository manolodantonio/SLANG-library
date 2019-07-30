package com.manzo.slang.extensions

import android.support.v4.widget.NestedScrollView
import android.view.View

/**
 * Created by Manolo D'Antonio on 19/07/2019
 */

/**
 * Animated scroll to bottom of content.
 * @receiver NestedScrollView
 * @param delay set to 1000 when changing already created UI
 */
fun NestedScrollView.scrollToBottom(delay: Long = 0) {
    postDelayed({ fullScroll(View.FOCUS_DOWN) }, delay)
}

/**
 * Animated scroll to top of content.
 * @receiver NestedScrollView
 * @param delay set to 1000 when changing already created UI
 */
fun NestedScrollView.scrollToTop(delay: Long = 0) {
    postDelayed({ fullScroll(View.FOCUS_UP) }, delay)
}

