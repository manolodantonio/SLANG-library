package com.manzo.slang.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.constraint.Group
import android.support.design.widget.Snackbar
import android.support.transition.AutoTransition
import android.support.transition.Transition
import android.support.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

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
 * Ex function for changing visibility by condition block
 */
fun View?.invisibleIf(shouldBeInvisible: () -> Boolean): View? {
    this ?: return this
    if (shouldBeInvisible.invoke()) invisible() else visible()
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
 * Ex function for changing visibility by condition block
 */
fun View?.goneIf(shouldBeGone: () -> Boolean): View? {
    this ?: return this
    if (shouldBeGone.invoke()) gone() else visible()
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
fun View?.withTransition(
    viewGroup: ViewGroup? = null,
    animationTime: Long = -1,
    transition: Transition? = null
) {
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
    transition: Transition? = null,
    onEndListener: (() -> Unit)? = null
) {
    this ?: return
    TransitionManager.beginDelayedTransition(
        this,
        run {
            (transition ?: AutoTransition())
                .apply { if (animationTime != -1L) this.duration = animationTime }
                .apply {
                    onEndListener?.let {
                        this.addListener(object : Transition.TransitionListener {
                            override fun onTransitionEnd(p0: Transition) {
                                it.invoke()
                            }

                            override fun onTransitionResume(p0: Transition) {
                                //not used
                            }

                            override fun onTransitionPause(p0: Transition) {
                                //not used
                            }

                            override fun onTransitionCancel(p0: Transition) {
                                //not used
                            }

                            override fun onTransitionStart(p0: Transition) {
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

var snackAvailable = true
/**
 * Shows a snackbar, SHORT by default
 * @receiver View
 * @param message String
 * @param duration Int
 * @param blockSnacksTimer will block other invocations of this function for the next N milliseconds
 *
 */
fun View.snack(message: String, duration: Int = Snackbar.LENGTH_SHORT, blockSnacksTimer: Long = 0) {
    if (snackAvailable) {
        if (blockSnacksTimer > 0) snackAvailable = false
        GlobalScope.launch(Dispatchers.Main) {
            Snackbar.make(this@snack, message, duration).show()
            if (blockSnacksTimer > 0) {
                launch {
                    delay(blockSnacksTimer)
                    snackAvailable = true
                }
            }
        }
    }
}

/**
 * Resource version of [View.snack]
 * @receiver View
 * @param message Int
 * @param duration Int
 * @param blockSnacksTimer Long
 */
fun View.snack(message: Int, duration: Int = Snackbar.LENGTH_SHORT, blockSnacksTimer: Long = 0) {
    snack(string(message), duration, blockSnacksTimer)
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
        requestOptions.error(it)
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
        is Uri -> with.load(parameter)
        is File -> with.load(parameter)
        is ByteArray -> with.load(parameter)
        is Bitmap -> with.load(parameter)
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
    viewTreeObserver.addOnGlobalLayoutListener { listener.invoke() }
}


/**
 * Uses [ViewTreeObserver.addOnGlobalLayoutListener] to check if a softKeyboard is going up or down.
 * @param view View
 * @param onShow result
 */
fun onSoftKeyboard(view: View, onShow: (isUp: Boolean) -> Unit) {
    view.onGlobalLayout {
        val r = Rect()
        view.getWindowVisibleDisplayFrame(r)
        val heightDiff = view.rootView.height - (r.bottom - r.top)

        // if more than 200 pixels, it's probably a keyboard...
        onShow(heightDiff > 200)
    }
}