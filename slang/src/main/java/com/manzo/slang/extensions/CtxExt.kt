package com.manzo.slang.extensions

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.preference.PreferenceManager
import android.support.annotation.*
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.view.View
import android.widget.Toast
import java.io.File


/**
 * Shows a toast, SHORT by default
 * @receiver Context
 * @param message String
 * @param isLongDuration Boolean
 */
fun Context.toast(message: String, isLongDuration: Boolean = false) =
    Toast.makeText(this, message, if (isLongDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()


/**
 * Shows a snackbar, SHORT by default
 * @receiver View
 * @param message String
 * @param duration Int
 */
fun View.snack(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}


/**
 * Get version app number as string
 */
fun Context.getAppVersion(): String =
    packageManager.getPackageInfo(packageName, 0).versionName

/**
 * Extension function for easier resource acquirement
 */
fun Context.dimen(@DimenRes resource: Int): Int {
    return resources.getDimension(resource).toInt()
}

/**
 * Extension function for easier resource acquirement
 */
fun Context.color(@ColorRes resource: Int): Int {
    return ContextCompat.getColor(this, resource)
}

/**
 * Extension function for easier resource acquirement
 */
fun Context.drawable(@DrawableRes resource: Int): Drawable? {
    return ContextCompat.getDrawable(this, resource)
}

/**
 * Extension function for easier resource acquirement
 */
fun Context.string(@StringRes resource: Int): String {
    return getString(resource)
}

/**
 * Extension function for easier resource acquirement
 */
fun Context.string(@StringRes resource: Int, vararg replacements: String): String {
    return getString(resource, *replacements)
}

/**
 * Extension function for easier resource acquirement
 */
fun Context.boolean(@BoolRes resource: Int): Boolean {
    return resources.getBoolean(resource)
}


/**
 * Check if fingerprint auth is available
 */
fun Context.isFingerprintAvailable(): Boolean {
    return if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N)
        false
    else
        FingerprintManagerCompat.from(this).isHardwareDetected

}


/**
 * Returns default shared preferences
 * @receiver Context
 * @return (android.content.SharedPreferences..android.content.SharedPreferences?)
 */
fun Context.defaultPrefs(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)


/**
 *
 * @receiver Context
 * @param filename String
 * @return File
 */
fun Context.getInternalFile(filename: String) = File(externalCacheDir, filename).apply { createNewFile() }


