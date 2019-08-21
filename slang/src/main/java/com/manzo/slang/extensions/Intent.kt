package com.manzo.slang.extensions

import android.content.Context
import android.content.Intent


/**
 * Starts intent if a receiver activity is available.
 * @receiver Intent
 * @param context Context
 * @return True if activity started.
 */
fun Intent.start(context: Context): Boolean {
    return resolveActivity(context.packageManager)?.let {
        context.startActivity(this)
        true
    } ?: run {
        "No activity to resolve intent!".logError()
        false
    }
}