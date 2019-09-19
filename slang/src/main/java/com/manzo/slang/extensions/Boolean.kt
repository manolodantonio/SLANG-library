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
    takeIf { it }?.run { block.invoke() }
}


/**
 * Runs the block only if boolean is false
 * @receiver Boolean
 * @param block Function0<Unit>
 */
fun Boolean.ifFalse(block: () -> Unit) {
    takeIf { !it }?.run { block.invoke() }
}