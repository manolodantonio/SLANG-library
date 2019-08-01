package com.manzo.slang.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.util.Log

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
): Boolean {
    // SENDTO doesn't accept attachments
    return Intent(attachFileUri?.run { Intent.ACTION_SEND } ?: Intent.ACTION_SENDTO)
        .apply {
            attachFileUri
                ?.let {
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_STREAM, it)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                ?: run { data = Uri.parse("mailto:") }
            recipients?.let { putExtra(Intent.EXTRA_EMAIL, it) }
            subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
            message?.let { putExtra(Intent.EXTRA_TEXT, it) }

        }
        .start(this)
}

/**
 *
 * @receiver Activity
 * @param phone String
 * @return Boolean
 */
fun Activity.dialPhoneNumber(phone: String): Boolean {
    return Intent(Intent.ACTION_DIAL)
        .apply {
            data = Uri.parse("tel:$phone")
        }
        .start(this)
}


/**
 *
 * @receiver Activity
 * @param address String
 * @return Boolean
 */
fun Activity.navigateToAddress(address: String): Boolean {
    val gmmIntentUri = Uri.parse("google.navigation:q=" + address.replace(" ", "+"))
    return Intent(Intent.ACTION_VIEW, gmmIntentUri)
        .apply { setPackage("com.google.android.apps.maps") }
        .start(this)
}

/**
 *
 * @receiver Activity
 * @param text String
 * @param title String?
 * @param imageUri Uri?
 * @return Boolean
 */
fun Activity.shareText(text: String, title: String? = null, imageUri: Uri? = null): Boolean {
    return Intent(Intent.ACTION_SEND)
        .apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)

            title?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
            imageUri?.let {
                putExtra(Intent.EXTRA_STREAM, it)
                type = "image/*"
            }

        }
        .start(this)

}

/**
 *
 * @receiver Intent
 * @param context Context
 * @return Boolean
 */
private fun Intent.start(context: Context): Boolean {
    return resolveActivity(context.packageManager)?.let {
        context.startActivity(this)
        true
    } ?: false
}

/**
 *
 * @param fragmentOrActivity Any?
 * @param requestCode Int
 * @param permissions Array<out String>
 * @return Boolean
 */
fun checkPermissions(fragmentOrActivity: Any?, requestCode: Int, vararg permissions: String): Boolean {
    val context = when (fragmentOrActivity) {
        is Fragment -> fragmentOrActivity.context
        is Activity -> fragmentOrActivity
        else -> {
            Log.e("checkPermission Error", "fragmentOrActivity.. is not a Fragment or an Activity!")
            return false
        }
    } ?: return false

    return if (context.hasPermissions(*permissions)) {
        true
    } else {
        when (fragmentOrActivity) {
            is Fragment -> fragmentOrActivity.requestPermissions(permissions, requestCode)
            is Activity -> ActivityCompat.requestPermissions(fragmentOrActivity, permissions, requestCode)
        }
        false
    }
}

