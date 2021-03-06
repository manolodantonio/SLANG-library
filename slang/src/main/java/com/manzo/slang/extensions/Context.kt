package com.manzo.slang.extensions

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.preference.PreferenceManager
import android.support.annotation.*
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import com.manzo.slang.navigation.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream


var toastAvailable = true

fun Context.toast(message: Int, isLongDuration: Boolean = false, blockToastsTimer: Long = 0) {
    toast(string(message), isLongDuration, blockToastsTimer)
}

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
            Toast.makeText(
                this@toast,
                message,
                if (isLongDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            ).show()
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
 * Extension function for easier resource acquirement
 *
 * Read as text with [InputStream.text]
 */
fun Context.raw(@RawRes resource: Int): InputStream {
    return resources.openRawResource(resource)
}

/**
 * Extension function for easier resource acquirement
 *
 * Read as text with [InputStream.text]
 */
fun Context.asset(filename: String): InputStream {
    return assets.open(filename)
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
fun Context.defaultPrefs(): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(applicationContext)


/**
 * Saves app log to target internal file. Creates the file if needed.
 * Set the file content or [append]s to its content.
 * @receiver Context
 * @param targetFile String
 * @param append Boolean
 * @return File
 */
fun Context.saveLogsToFile(targetFile: String, append: Boolean = false) =
    getLogs().writeToInternalFile(this, targetFile, append)


/**
 * Returns or creates file from the external cache dir
 * @receiver Context
 * @param filename String
 * @return File
 */
fun Context.getInternalFile(filename: String) =
    File(externalCacheDir, filename).apply { createNewFile() }

/**
 * Checks if user has granted permissions. ie: Manifest.permission.ACCESS_FINE_LOCATION
 * @receiver Context
 * @param permissions Array<out String>
 * @return Boolean
 */
fun Context.hasPermissions(vararg permissions: String): Boolean {
    for (arg in permissions) {
        if (ContextCompat.checkSelfPermission(
                this,
                arg
            ) == PackageManager.PERMISSION_DENIED
        ) return false
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
 * Digs through the hierarchy to find the base activity of a context.
 *
 * May return null.
 * @receiver Context
 * @return Activity?
 */
fun Context.getParentActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

/**
 * Starts new activity.
 *
 * If context is not an Activity, tries to find parent activity. If a parent activity cannot be found, starts the target activity with FLAG_ACTIVITY_NEW_TASK
 * @receiver Context
 */
inline fun <reified T : Activity> Context.startActivity() {
    lateinit var startingContext: Context
    Intent(this, T::class.java)
        .apply {
            startingContext = when (this) {
                is View -> {
                    context.getParentActivity() ?: run {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        this@startActivity
                    }
                }
                is Fragment -> {
                    if (this is BaseFragment) activity
                    else activity ?: run {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        this@startActivity
                    }
                }
                is Activity, is Application -> this@startActivity
                else -> getParentActivity() ?: run {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    this@startActivity
                }
            }
        }
        .start(startingContext)
}


/**
 * Checks if device is in landscape orientation
 * @receiver Context
 * @return Boolean
 */
fun Context.isLandscape() =
    resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

/**
 * Checks if device is in portrait orientation
 * @receiver Context
 * @return Boolean
 */
fun Context.isPortrait() =
    resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT


/**
 * Broadcasts an intent locally. Data is added to extras as strings.
 * @receiver Context
 * @param intentFilter String
 * @param data Map<String, String>
 */
fun Context.sendLocalBroadcast(intentFilter: String, data: Map<String, String>? = null) {
    Intent(intentFilter)
        .apply {
            data?.forEach { entry ->
                putExtra(entry.key, entry.value)
            }
        }
        .let {
            LocalBroadcastManager.getInstance(this).sendBroadcast(it)
        }
}


/**
 * Convenience function for broadcasting an intent locally.
 * @receiver Context
 * @param intent Intent
 */
fun Context.sendLocalBroadcast(intent: Intent) {
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
}

@Suppress("DEPRECATION") // Deprecated for third party Services.
inline fun <reified T> Context.isServiceRunningForeground() =
    (getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)
        ?.getRunningServices(Integer.MAX_VALUE)
        ?.find { it.service.className == T::class.java.name }
        ?.foreground == true
