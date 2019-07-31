package com.manzo.slang.extensions

import java.util.concurrent.TimeUnit

/**
 * Created by Manolo D'Antonio on 30/07/2019
 */

/**
 * Convert long to timeframe. Default format HH:MM:SS(.MS)
 * @receiver Long
 * @param showMillis Boolean
 * @param stringFormat String?
 * @return String
 */
fun Long.toTimeframe(showMillis: Boolean = false, stringFormat: String? = null): String {
    val format = stringFormat ?: "%01d:%02d:%02d" + if (showMillis) ".%d" else ""

    val hours = TimeUnit.MILLISECONDS.toHours(this) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60
    val milliseconds = this % 1000

    return String.format(format, hours, minutes, seconds, milliseconds)
}

/**
 *
 * @receiver Long
 * @return Long
 */
fun Long.millisToSeconds() = TimeUnit.MILLISECONDS.toSeconds(this)

/**
 *
 * @receiver Long
 * @return Long
 */
fun Long.millisToMinutes() = TimeUnit.MILLISECONDS.toMinutes(this)

/**
 *
 * @receiver Long
 * @return Long
 */
fun Long.secondsToMinutes() = TimeUnit.SECONDS.toMinutes(this)