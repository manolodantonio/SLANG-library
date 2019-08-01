package com.manzo.slang.extensions.gears

import android.app.Activity
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.util.Log
import com.manzo.slang.extensions.hasPermissions

/**
 * Created by Manolo D'Antonio on 01/08/2019
 */


/**
 *
 * @param fragmentOrActivity Any?
 * @param requestCode Int
 * @param permissions Array<out String>
 * @return Boolean
 */
internal fun checkPermissions(fragmentOrActivity: Any?, requestCode: Int, vararg permissions: String): Boolean {
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
