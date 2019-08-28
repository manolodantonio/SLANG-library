package com.manzo.slang.navigation

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.manzo.slang.extensions.closeKeyboard

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
                    if (newStack)
                        replace(containerResource, fragment, canonicalName)
                    else {
                        //                            setCustomAnimations(R.anim.slide_enter_right, R.anim.slide_exit_right,  R.anim.slide_enter_right, R.anim.slide_exit_right)
                        addToBackStack(canonicalName)
                        add(containerResource, fragment, canonicalName)
                            .show(fragment)
                    }
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

    fun <T> navigateBackTo(targetFragment: Class<T>): Boolean {
        return supportFragmentManager.run {
            findFragmentByTag(targetFragment.simpleName)?.let {
                popBackStackImmediate(targetFragment.simpleName, 0).run {
                    if (!this && fragments.size > 1) {
                        // if fail to pop
                        // fragment is in stack but no state: is fragment 0.
                        fragments[1]?.let { first ->
                            popBackStackImmediate(first.javaClass.simpleName, POP_BACK_STACK_INCLUSIVE)
                        }

                    } else this
                }
            } ?: false
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.run {
            if (isEmpty()) super.onBackPressed()
            else (last() as BaseFragment)
                .onFragmentBackPressed()
                .takeIf { managed -> managed.not() }
                ?.run { super.onBackPressed() }
        }
    }

}

