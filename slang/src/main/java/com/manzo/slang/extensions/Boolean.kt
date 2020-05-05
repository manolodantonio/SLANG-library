package com.manzo.slang.extensions

/**
 * Created by Manolo D'Antonio on 17/09/2019
 */

/**
 * Runs the block only if boolean is true
 * @receiver Boolean
 * @param block Function0<Unit>
 */
fun Boolean.ifTrue(block: () -> Unit) {
    takeIf { it }?.run { block() }
}


/**
 * Runs the block only if boolean is false
 * @receiver Boolean
 * @param block Function0<Unit>
 */
fun Boolean.ifFalse(block: () -> Unit) {
    takeIf { !it }?.run { block() }
}


/**
 * Returns true = 1, false = 0
 * @receiver Boolean
 * @return Int
 */
fun Boolean.toInt() = if (this) 1 else 0