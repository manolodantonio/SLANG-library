package com.manzo.slang.extensions

import android.graphics.drawable.Drawable

/**
 * Extension function to copy a drawable
 */
fun Drawable.copy() =
        mutate().constantState?.newDrawable()


