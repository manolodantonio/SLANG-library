package com.manzo.slang.extensions

import android.content.res.ColorStateList
import android.support.v4.widget.ImageViewCompat
import android.widget.ImageView

/**
 * Created by Manolo D'Antonio on 05/05/2020
 */


/**
 * Add tint to ImageView. Pre api 21 compatible.
 * @receiver ImageView
 * @param colorResource Int
 */
fun ImageView.tint(colorResource: Int) =
    ImageViewCompat.setImageTintList(this, (ColorStateList.valueOf(context.color(colorResource))))
