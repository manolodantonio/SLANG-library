package com.manzo.slang.navigation

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.manzo.slang.extensions.closeKeyboard
import com.manzo.slang.extensions.ifFalse

/**
 * Created by Manolo D'Antonio
 */


abstract class BaseActivity : AppCompatActivity() {
    var isHamburgerIcon = true
    var drawerToggle: ActionBarDrawerToggle? = null

    protected abstract fun getFragmentContainer(): Int?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }


    protected open val localBroadcastIntentFilter: String? = null
    private val localBroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, intent: Intent?) {
                onLocalBroadcastReceive(intent)
            }
        }
    }

    open fun onLocalBroadcastReceive(intent: Intent?) {}


    override fun onResume() {
        super.onResume()
        //
        localBroadcastIntentFilter?.let {
            LocalBroadcastManager.getInstance(this)
                .registerReceiver(localBroadcastReceiver, IntentFilter(it))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //
        localBroadcastIntentFilter?.let {
            LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(localBroadcastReceiver)
        }
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun closeApplication(activity: Activity) {
        activity.run {
            AlertDialog.Builder(this)
                .setTitle("Exit the app?")
                .setPositiveButton("OK") { _, _ ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAndRemoveTask()
                    } else {
                        finishAffinity()
                    }
                }
                .setNegativeButton("Stay") { _, _ -> }
                .show()


        }
    }

    fun addFragment(
        fragment: BaseFragment,
        container: Int = -1,
        cleanStack: Boolean? = null
    ): Boolean {
        closeKeyboard()

        val newStack = cleanStack ?: (supportFragmentManager.fragments.size == 0)
        val containerResource =
            if (container != -1) container
            else getFragmentContainer()
                ?: -1
        if (containerResource != -1) {
            val canonicalName = fragment.javaClass.simpleName

            supportFragmentManager
                .beginTransaction()
                .run {
                    //                    if (newStack)
//                        replace(containerResource, fragment, canonicalName)
//                    else {
//                        //                            setCustomAnimations(R.anim.slide_enter_right, R.anim.slide_exit_right,  R.anim.slide_enter_right, R.anim.slide_exit_right)
//                        addToBackStack(canonicalName)
//                        add(containerResource, fragment, canonicalName)
//                            .show(fragment)
//                    }

                    if (!newStack) addToBackStack(canonicalName)
                    add(containerResource, fragment, canonicalName)
                        .show(fragment)
                }
                .commit()

        } else {
            throw Exception("addFragment: set getFragmentContainer before using this function! Or use 'container' parameter.")
        }

        return newStack
    }

    fun navigateBack(): Boolean {
        return supportFragmentManager.run {
            if (backStackEntryCount > 0) {
                popBackStack()
                true
            } else false
        }
    }

    fun <T> navigateBackTo(targetFragment: Class<T>) {
        supportFragmentManager.run {
            findFragmentByTag(targetFragment.simpleName)?.let {
                popBackStackImmediate(targetFragment.simpleName, 0).run {
                    if (!this && fragments.size > 1) {
                        // if fail to pop
                        // fragment is in stack but no state: is fragment 0.
                        // fragments in backstack and status to pop are NOT the same thing.
                        // we are not adding status for first fragment,
                        // doing that will cause blank screen on back.
                        // So, horrible solution for an horrible problem:
                        repeat(fragments.size - 1) {
                            popBackStack()
                        }

                    } else super.onBackPressed()
                }
            } ?: super.onBackPressed()
        }
    }


    override fun onBackPressed() {
        supportFragmentManager.run {
            when {
                backStackEntryCount == 0 ->
                    super.onBackPressed()
                fragments.last() is BaseFragment ->
                    (fragments.last() as BaseFragment).onFragmentBackPressed()
                        .ifFalse { super.onBackPressed() }
                else ->
                    super.onBackPressed()
            }
        }
    }
}

