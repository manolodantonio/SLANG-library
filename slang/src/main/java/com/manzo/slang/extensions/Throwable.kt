package com.manzo.slang.extensions

import android.util.Log


/**
 * Logs the exception. A tag is automatically assigned if not provided.
 * @receiver Exception
 * @param tag String
 */
fun Throwable.logError(tag: String = javaClass.simpleName) {
    Log.e(tag, getError())
}

/**
 * Get the best error possible
 * @receiver Exception
 * @return String
 */
fun Throwable.getError(): String {
    return when {
        localizedMessage.isNotNullOrBlank() -> localizedMessage!!
        message.isNotNullOrBlank() -> message!!
        else -> ""
    }
}
