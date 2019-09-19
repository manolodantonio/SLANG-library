package com.manzo.slang.extensions

import android.support.v4.app.Fragment
import com.manzo.slang.extensions.gears.checkPermissions

/**
 * Created by Manolo D'Antonio on 13/09/2019
 */


/**
 * Fragment extension for [checkPermissions]
 * @receiver Fragment
 * @param requestCode Int
 * @param permissions Array<out String>
 * @return Boolean
 */
fun Fragment.checkPermissions(requestCode: Int, vararg permissions: String) =
    checkPermissions(this, requestCode, *permissions)
