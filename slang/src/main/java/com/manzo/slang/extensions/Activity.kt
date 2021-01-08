package com.manzo.slang.extensions

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import com.manzo.slang.PKG_NAME_GOOGLE_CHROME
import com.manzo.slang.extensions.gears.checkPermissions

/**
 * Created by Manolo D'Antonio
 */


/**
 * Activity extension for [checkPermissions]
 * @receiver Activity
 * @param requestCode Int
 * @param permissions Array<out String>
 * @return Boolean
 */
fun Activity.checkPermissions(requestCode: Int, vararg permissions: String) =
    checkPermissions(this, requestCode, *permissions)


/**
 * Hide softKeyboard. Needs [Activity.getCurrentFocus] to not be null.
 * @receiver Activity
 */
fun Activity.closeKeyboard() = currentFocus?.closeKeyboard()
    ?: "Cannot find currentFocus".logError("closeKeyboard")

/**
 * Closes the application
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
fun Activity.closeApplication() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        finishAndRemoveTask()
    } else {
        finishAffinity()
    }
}

/**
 * Starts email composer with default email
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
 * Starts default phone call composer
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
 * Opens google maps and navigates to the provided street address
 * @receiver Activity
 * @param address String
 * @return Boolean
 */
fun Activity.navigateToStreetAddress(address: String): Boolean {
    val gmmIntentUri = Uri.parse("google.navigation:q=" + address.replace(" ", "+"))
    return Intent(Intent.ACTION_VIEW, gmmIntentUri)
        .apply { setPackage("com.google.android.apps.maps") }
        .start(this)
}

/**
 * Opens share options for plain text and optional image
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
 * @receiver Context
 * @param address String : Url to open
 * @param openWithChrome Boolean : Force open with Google Chrome. Default false
 * @return Boolean : operation success
 */
fun Activity.navigateToUrl(address: String, openWithChrome: Boolean = false): Boolean {
    return Intent(Intent.ACTION_VIEW, Uri.parse(address))
        .apply {
            if (openWithChrome && checkAppInstalled(PKG_NAME_GOOGLE_CHROME))
                setPackage(PKG_NAME_GOOGLE_CHROME)
        }
        .start(this)
}

/**
 * Check package manager for app package name
 * @receiver Context
 * @param packageName String
 * @param minVersion Int : optional. Check if app version is at least this value.
 * @return Boolean : true if is installed
 */
fun Activity.checkAppInstalled(packageName: String, minVersion: Int = -1): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).run {
            if (minVersion == -1) true
            else minVersion <= versionName.split(".")[0].toInt()
        }
    } catch (e: Exception) {
        false
    }
}


