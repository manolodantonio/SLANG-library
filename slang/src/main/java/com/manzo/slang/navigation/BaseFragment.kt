package com.manzo.slang.navigation

import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.manzo.slang.extensions.inflate
import com.manzo.slang.extensions.string

/**
 * Created by Manolo D'Antonio
 */


interface FragmentOnBackPressed {
    fun onFragmentBackPressed(): Boolean
}

abstract class BaseFragment : Fragment(), FragmentOnBackPressed {
    override fun onFragmentBackPressed() = false

    var positionInBackstack = -1

    val activity by lazy { this.getActivity() as BaseActivity }

    protected abstract fun setLayout(): Int
    protected abstract fun setupInterface(view: View, savedInstanceState: Bundle?)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if (view == null) container?.inflate(setLayout())
        else view
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupInterface(view, savedInstanceState)
    }

    private lateinit var backstackChangeListener: FragmentManager.OnBackStackChangedListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentManager?.run {
            // fragments added via REPLACE and will not fire backstack changes fix
            if (backStackEntryCount == 0 && positionInBackstack == -1) {
                onFragmentResume()
            }
            // when fragment is attached, remember position in backstack
            positionInBackstack = backStackEntryCount

            FragmentManager.OnBackStackChangedListener {
                if (isAdded) {
                    showHamburgerIcon(backStackEntryCount == 0)
                    when (backStackEntryCount) {
                        positionInBackstack -> {
                            // when position of this fragment is reached, call resume
                            onFragmentResume()
                        }
//                            backStackEntryCount < positionInBackstack ->
//                                // if fragment is popped, remove listener to avoid false positives
//                                removeOnBackStackChangedListener(this)
                    }
                }
            }.run {
                backstackChangeListener = this
                addOnBackStackChangedListener(backstackChangeListener)
            }

        }
    }

    override fun onDetach() {
        // if fragment is popped, remove listener to avoid false positives
        fragmentManager?.removeOnBackStackChangedListener(backstackChangeListener)
        super.onDetach()
    }

    open fun onFragmentResume() {
        //implement when needed
    }

    fun isCurrentFragment() = positionInBackstack == fragmentManager?.backStackEntryCount ?: false

    fun navigateBack() {
        activity.navigateBack()
    }

    fun navigateTo(fragment: BaseFragment, newStack: Boolean? = null): Boolean {
        return activity.addFragment(fragment, cleanStack = newStack)
    }

    private fun showHamburgerIcon(show: Boolean) {
        activity.run {
            isHamburgerIcon = show
            drawerToggle?.drawerArrowDrawable?.progress =
                if (show) 0f else 1f
        }
    }

//    open fun call(controller: BaseController<*, *>) {
//        controller.init(false, IS_DEBUG)
//    }



//    override fun getContext(): Context {
//        return super.getContext() ?: ApplicationOverride.applicationContext()
//    }

    fun string(@StringRes stringRes: Int, vararg replacements: String): String =
        context?.string(stringRes, *replacements) ?: ""

}

