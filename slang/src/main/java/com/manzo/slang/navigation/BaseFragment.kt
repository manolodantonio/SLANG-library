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
import com.manzo.slang.navigation.FragmentAddMethod.ADD_WITH_BACKSTACK
import com.manzo.slang.navigation.FragmentAddMethod.CLEAR_STACK

/**
 * Created by Manolo D'Antonio
 */


interface FragmentOnBackPressed {
    fun onFragmentBackPressed(): Boolean
}

/**
 * To be used in pair with [BaseActivity]. This will offload the majority of the boilerplate for fragments.
 *
 * Example implementation:
 *
 * ```
 * class testFragment : BaseFragment() {
 *  override fun setLayout(): Int {
 *     return R.layout.your_fragment_layout
 *  }
 *
 *  override fun setupInterface(view: View, savedInstanceState: Bundle?) {
 *        //Here you can work on the interface like you would in onViewCreated
 *  }
 * }
 * ```
 *
 * @property activity BaseActivity
 * @property backstackChangeListener OnBackStackChangedListener
 */
abstract class BaseFragment : Fragment(), FragmentOnBackPressed {
    override fun onFragmentBackPressed() = false

    private var positionInBackstack = -1

    val activity by lazy { getActivity() as BaseActivity }

    /**
     * set layout resource for the fragment. IE:
     * ```
     *  override fun setLayout() = R.layout.fragment_layout
     * ```
     *
     * @return Int
     */
    protected abstract fun setLayout(): Int

    /**
     * Here you can work on the interface like you would in onViewCreated
     * @param view View
     * @param savedInstanceState Bundle?
     */
    protected abstract fun setupInterface(view: View, savedInstanceState: Bundle?)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            // fix for: fragments added via REPLACE will not fire backstack changes
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

    /**
     * Check if this fragment is the current backstack entry
     * @return Boolean
     */
    fun isCurrentFragment() = positionInBackstack == fragmentManager?.backStackEntryCount ?: false

    /**
     * Convenience function for [BaseActivity.navigateBack]
     */
    fun navigateBack() {
        activity.navigateBack()
    }

    /**
     * Alternative constructor for [BaseActivity.addFragment]
     * @param fragment BaseFragment
     * @param clearStack Boolean?
     * @return Boolean
     */
    fun navigateTo(fragment: BaseFragment, clearStack: Boolean? = null): Boolean {
        val addMethod =
            clearStack?.let { if (it) CLEAR_STACK else ADD_WITH_BACKSTACK } ?: ADD_WITH_BACKSTACK
        return activity.addFragment(fragment, addMethod = addMethod)
    }

    private fun showHamburgerIcon(show: Boolean) {
        activity.run {
            isHamburgerIcon = show
            drawerToggle?.drawerArrowDrawable?.progress =
                if (show) 0f else 1f
        }
    }

    /**
     * Convenience function for [Context.string]
     * @param stringRes Int
     * @param replacements Array<out String>
     * @return String
     */
    fun string(@StringRes stringRes: Int, vararg replacements: String): String =
        context?.string(stringRes, *replacements) ?: ""

}

