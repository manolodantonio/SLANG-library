package com.manzo.slang.extensions

import android.support.v4.app.FragmentManager

/**
 * Created by Manolo D'Antonio on 19/11/2019
 */


/**
 * Removes all fragments from the provided [FragmentManager] backstack
 * @receiver FragmentManager
 */
fun FragmentManager.clearBackstack() {
    if (backStackEntryCount > 0) {
        popBackStack(
            getBackStackEntryAt(0).id,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
}