package com.manzo.slang.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Created by Manolo D'Antonio
 */

/**
 * Hide softKeyboard
 * @receiver Activity
 */
fun Activity.closeKeyboard() = currentFocus?.closeKeyboard()



/**
 *
 * @receiver Activity
 * @param recipients Array<String>?
 * @param subject String?
 * @param message String?
 * @param attachFileUri Uri?
 */
fun Activity.sendEmail(
    recipients: Array<String>? = null,
    subject: String? = null,
    message: String? = null,
    attachFileUri: Uri? = null
) {
    Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"

        recipients?.let { putExtra(Intent.EXTRA_EMAIL, it) }
        subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
        message?.let { putExtra(Intent.EXTRA_TEXT, it) }
        attachFileUri?.let {
            putExtra(Intent.EXTRA_STREAM, it)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

    }.run {
        startActivity(this)
    }
}

/**
 * Starts new activity and clears previous task.
 * @receiver Context
 */
inline fun <reified T : Activity> Context.startActivityNewTask() {
    Intent(this, T::class.java)
        .apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let {
            startActivity(it)
        }
}

/**
 * Starts new activity. Adds FLAG_ACTIVITY_NEW_TASK when trying to start new activity from out of Activity context
 * @receiver Context
 */
inline fun <reified T : Activity> Context.startActivity() {
    Intent(this, T::class.java)
        .apply {
            if (this !is Activity) flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }.let {
            startActivity(it)
        }
}

