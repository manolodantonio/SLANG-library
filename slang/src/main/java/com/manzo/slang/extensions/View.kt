package com.manzo.slang.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.constraint.Group
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/**
 * Hide softKeyboard
 * @receiver View
 */
fun View.closeKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(windowToken, 0)
}


/**
 * Extension function to check visibility
 */
fun View.isVisible(): Boolean = visibility == View.VISIBLE

/**
 * Ex function for changing visibility
 */
fun View?.visible(): View? {
    this ?: return this
    visibility = View.VISIBLE
    return this
}

/**
 * Ex function for changing visibility
 */
fun View?.invisible(): View? {
    this ?: return this
    visibility = View.INVISIBLE
    return this
}

/**
 * Ex function for changing visibility
 */
fun View?.gone(): View? {
    this ?: return this
    visibility = View.GONE
    return this
}


/**
 * Ex function for changing visibility by condition
 */
fun View?.invisibleIf(shouldBeInvisible: Boolean): View? {
    this ?: return this
    if (shouldBeInvisible) invisible() else visible()
    return this
}

/**
 * Ex function for changing visibility by condition
 */
fun View?.goneIf(shouldBeGone: Boolean): View? {
    this ?: return this
    if (shouldBeGone) gone() else visible()
    return this
}

/**
 * Extension function for switching visible\invisible visibility
 */
fun View?.switchInvisibleVisible(): View? {
    this ?: return this
    if (visibility == View.VISIBLE) invisible() else visible()
    return this
}


/**
 * Extension function for switching visible\gone visibility
 */
fun View?.switchGoneVisible(): View? {
    this ?: return this
    if (visibility == View.VISIBLE) gone() else visible()
    return this
}

/**
 * Ex function for adding transition
 *
 * concatenate this to visibility extensions
 *
 * ie: view.gone().withTransition()
 */
fun View?.withTransition(viewGroup: ViewGroup? = null, animationTime: Long = -1, transition: android.support.transition.Transition? = null) {
    this ?: return
    when {
        viewGroup != null -> viewGroup.startTransition(animationTime, transition)
        parent is ViewGroup -> (parent as ViewGroup).startTransition(animationTime, transition)
        else -> Log.e("startTransition", "Parent is not a ViewGroup. Use withTransition(viewGroup)")
    }
}


/**
 * Convenience function for transition manager
 */
fun ViewGroup?.startTransition(
    animationTime: Long = -1,
    transition: android.support.transition.Transition? = null,
    onEndListener: (() -> Unit)? = null
) {
    this ?: return
    android.support.transition.TransitionManager.beginDelayedTransition(
        this,
        run {
            (transition ?: android.support.transition.AutoTransition())
                .apply { if (animationTime != -1L) this.duration = animationTime }
                .apply {
                    onEndListener?.let {
                        this.addListener(object : android.support.transition.Transition.TransitionListener {
                            override fun onTransitionEnd(p0: android.support.transition.Transition) {
                                it.invoke()
                            }

                            override fun onTransitionResume(p0: android.support.transition.Transition) {
                                //not used
                            }

                            override fun onTransitionPause(p0: android.support.transition.Transition) {
                                //not used
                            }

                            override fun onTransitionCancel(p0: android.support.transition.Transition) {
                                //not used
                            }

                            override fun onTransitionStart(p0: android.support.transition.Transition) {
                                //not used
                            }

                        })
                    }

                }
        }
    )
}

/**
 * Inflates layout into parent ViewGroup
 */
fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return this.run { LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot) }
}

/**
 * Extension function for snackbars
 */
inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}


/**
 * Extension method to provide simpler access to {@link View#getResources()#getString(int)}.
 */
fun View.string(stringResId: Int): String = context.string(stringResId)



/**
 * Extension function for Glide Builder:
 * @param parameter can be a Drawable, Resource Int or a Uri\URL String
 */
@SuppressLint("CheckResult")
fun ImageView.image(
    parameter: Any?,
    @DrawableRes placeHolder: Int? = null,
    @DrawableRes errorPlaceHolder: Int? = null,
    fitStyle: FitStyle? = null
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && parameter is VectorDrawable) {
        setImageDrawable(parameter)
        return
    }

    val with = Glide.with(this)

    val requestOptions = RequestOptions()

    placeHolder?.let {
        requestOptions.placeholder(it)
    }
    errorPlaceHolder?.let {
        requestOptions.error(it).fallback(it)
    }

    fitStyle.apply {
        when (this) {
            FitStyle.CENTER_CROP -> requestOptions.centerCrop()
            FitStyle.CENTER_INSIDE -> requestOptions.centerInside()
            FitStyle.CIRCLE_CROP -> requestOptions.circleCrop()
            FitStyle.FIT_CENTER -> requestOptions.fitCenter()
        }
    }

    val requestBuilder = when (parameter) {
        is Int -> with.load(parameter)
        is String -> with.load(parameter)
        is Drawable -> with.load(parameter)
        else -> with.load(errorPlaceHolder)
    }
    requestBuilder.apply(requestOptions).into(this)
}

/**
 * Used in [ImageView.image] extension
 */
enum class FitStyle { CENTER_CROP, CIRCLE_CROP, FIT_CENTER, CENTER_INSIDE }

/**
 * Add click listener to all group members
 */
fun Group.setAllOnClickListener(typeClickListener: (view: View) -> Unit): Group {
    referencedIds.forEach { id ->
        rootView.findViewById<View>(id).apply {
            setOnClickListener { typeClickListener.invoke(it) }
        }
    }
    return this
}

/**
 * Convenience function for global observer
 */
fun View.onGlobalLayout(listener: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            listener.invoke()
        }
    })
}