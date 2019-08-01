package com.manzo.slang.extensions

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.preference.PreferenceManager
import android.support.annotation.*
import android.support.v4.content.ContextCompat
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.util.TypedValue
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


var toastAvailable = true

/**
 * Shows a toast, SHORT by default. Avoids "java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()"
 * @receiver Context
 * @param message String
 * @param isLongDuration Boolean
 * @param blockToastsTimer will block other invocations of this function for the next N milliseconds
 *
 */
fun Context.toast(message: String, isLongDuration: Boolean = false, blockToastsTimer: Long = 0) {
    if (toastAvailable) {
        if (blockToastsTimer > 0) toastAvailable = false
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(this@toast, message, if (isLongDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
            if (blockToastsTimer > 0) {
                launch {
                    delay(blockToastsTimer)
                    toastAvailable = true
                }
            }
        }
    }
}



/**
 * Get version app number as string
 */
fun Context.getAppVersion(): String =
    packageManager.getPackageInfo(packageName, 0).versionName

/**
 * Get dimension res in DP
 */
fun Context.dimen(@DimenRes resource: Int) =
    resources.getDimension(resource)

/**
 * Get dimension res in PX
 */
fun Context.dimenPX(@DimenRes resource: Int) =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dimen(resource),
        resources.displayMetrics
    )

/**
 * Extension function for easier resource acquirement
 */
fun Context.color(@ColorRes resource: Int) = ContextCompat.getColor(this, resource)

/**
 * Extension function for easier resource acquirement
 */
fun Context.drawable(@DrawableRes resource: Int) = ContextCompat.getDrawable(this, resource)

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
@TargetApi(Build.VERSION_CODES.M)
@RequiresPermission(Manifest.permission.USE_FINGERPRINT)
fun Context.isFingerprintAvailable(): Boolean {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
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
 * Save app log to target file
 * @receiver Activity
 * @param targetFile String
 * @return File
 */
fun Context.saveLogsToFile(targetFile: String) =
    getLogs().writeToInternalFile(this, targetFile)


/**
 *
 * @receiver Context
 * @param filename String
 * @return File
 */
fun Context.getInternalFile(filename: String) = File(externalCacheDir, filename).apply { createNewFile() }

/**
 * Checks if user has granted permissions. ie: Manifest.permission.ACCESS_FINE_LOCATION
 * @receiver Context
 * @param permissions Array<out String>
 * @return Boolean
 */
fun Context.hasPermissions(vararg permissions: String): Boolean {
    for (arg in permissions) {
        if (ContextCompat.checkSelfPermission(this, arg) == PackageManager.PERMISSION_DENIED) return false
    }

    return true
}

/**
 * Negates [hasPermissions]
 * @receiver Context
 * @param permissions Array<out String>
 * @return Boolean
 */
fun Context.hasNotPermissions(vararg permissions: String): Boolean {
    return !hasPermissions(*permissions)
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
