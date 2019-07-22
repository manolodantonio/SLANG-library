package com.manzo.slang.extensions

import android.util.Log

/**
 * Created by Manolo D'Antonio on 19/07/2019
 */


/**
 * Logs the exception. A tag is automatically assigned if not provided.
 * @receiver Exception
 * @param tag String
 */
fun Exception.logError(tag: String = javaClass.simpleName) {
    Log.e(tag, getError())
}

/**
 * Get the best error possible
 * @receiver Exception
 * @return String
 */
fun Exception.getError(): String {
    return when {
        localizedMessage.isNotNullOrBlank() -> localizedMessage
        message.isNotNullOrBlank() -> message!!
        else -> ""
    }
}

