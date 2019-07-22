package com.manzo.slang.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri

/**
 * Created by Manolo D'Antonio on 19/07/2019
 */

/**
 * Hide softKeyboard
 * @receiver Activity
 */
fun Activity.closeKeyboard() = currentFocus?.closeKeyboard()



/**
 * Save app log to target file
 * @receiver Activity
 * @param targetFile String
 * @return File
 */
fun Activity.saveLogsToFile(targetFile: String) =
    getLogs().writeToInternalFile(this, targetFile)

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

