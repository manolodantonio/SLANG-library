package com.manzo.slang.navigation

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
import com.manzo.slang.extensions.*
import com.manzo.slang.navigation.FragmentAddMethod.*

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


    private val localBroadcast by lazy {
        LocalBroadcastManager.getInstance(this)
    }

    private val localBroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, intent: Intent?) {
                onLocalBroadcastReceive(intent)
            }
        }
    }

    /**
     * IntentFilter for the local Broadcasts. Override this value if you want to
     * receive broadcasts between BaseActivities. This is set as random Hex by default.
     */
    protected open val baseBroadcastIntentFilter: String = generateRandomHex()


    /**
     * Send broadcast to BaseActivities with same [baseBroadcastIntentFilter]. Extras in the provided
     * [intent] will be copied to a new intent with appropriate filter.
     * @param intent Intent
     */
    fun sendBaseBroadcast(intent: Intent) {
        Intent(baseBroadcastIntentFilter)
            .apply { putExtras(intent) }
            .let { localBroadcast.sendBroadcast(it) }
    }

    /**
     * Receives broadcasts sent via [sendBaseBroadcast]
     * @param intent Intent?
     */
    open fun onLocalBroadcastReceive(intent: Intent?) {}


    override fun onResume() {
        super.onResume()
        localBroadcast.registerReceiver(
            localBroadcastReceiver,
            IntentFilter(baseBroadcastIntentFilter)
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        localBroadcast.unregisterReceiver(localBroadcastReceiver)
    }


    /**
     * Closes the application with a standard warning dialog
     * @param activity Activity
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun closeApplicationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exit the app?")
            .setPositiveButton("OK") { _, _ ->
                closeApplication()
            }
            .setNegativeButton("Stay") { _, _ -> }
            .show()
    }


    /**
     * Adds [fragment] to the container provided in the override of [getFragmentContainer]. If [container]
     * is provided, it will be the priority target container.
     * @param fragment BaseFragment
     * @param container Int
     * @param addMethod FragmentAddMethod
     * @return Boolean
     */
    fun addFragment(
        fragment: BaseFragment,
        container: Int = -1,
        addMethod: FragmentAddMethod = ADD_WITH_BACKSTACK
    ): Boolean {
        closeKeyboard()

        val containerResource =
            if (container != -1) container
            else getFragmentContainer() ?: -1

        if (containerResource != -1) {
            val canonicalName = fragment.javaClass.simpleName

            val addToBackElab =
                addMethod == ADD_WITH_BACKSTACK && supportFragmentManager.fragments.size != 0

            supportFragmentManager
                .beginTransaction()
                .run {
                    when (addMethod) {
                        CLEAR_STACK, REPLACE -> {
                            if (addMethod == CLEAR_STACK) supportFragmentManager.clearBackstack()
                            replace(containerResource, fragment, canonicalName)
                        }
                        else -> {
                            if (addToBackElab) addToBackStack(canonicalName)
                            add(containerResource, fragment, canonicalName)
                                .show(fragment)
                        }
                    }
                }
                .commit()
            return true

        } else {
            throw Exception("addFragment: Container not found. Set getFragmentContainer before using this function, or use 'container' parameter.")
        }

    }


    /**
     * Navigates back to the last backstack status, but will not pop first fragment.
     * @return Boolean result of the operation
     */
    fun navigateBack(): Boolean {
        return supportFragmentManager.run {
            if (backStackEntryCount > 0) {
                popBackStack()
                true
            } else false
        }
    }

    /**
     * Finds fragment in the backstack and navigates back to it.
     * @param targetFragment Class<T>
     */
    fun <T> navigateBackTo(targetFragment: Class<T>) {
        supportFragmentManager.run {
            findFragmentByTag(targetFragment.simpleName)?.let {
                popBackStackImmediate(targetFragment.simpleName, 0).run {
                    if (!this && fragments.size > 1) {
                        // if fail to pop
                        // fragment is in stack but no state: is fragment 0 if using [navigateTo()].
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


    /**
     * This override is needed for [BaseFragment.onFragmentResume]
     */
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

/**
 * ADD_WITH_BACKSTACK - classical add
 *
 * ADD_WITHOUT_BACKSTACK - add, but status is not added to backstack (cannot be popped)
 *
 * REPLACE - replaces all fragments in the container
 *
 * CLEAR_STACK - same as replace, but also cleans fragmentManager backstack
 */

enum class FragmentAddMethod {
    ADD_WITH_BACKSTACK, ADD_WITHOUT_BACKSTACK, REPLACE, CLEAR_STACK
}