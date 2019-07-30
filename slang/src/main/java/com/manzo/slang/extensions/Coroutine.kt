package com.manzo.slang.extensions

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Created by Manolo D'Antonio on 19/07/2019
 */

/**
 * Wrapper interface used in [awaitCallback]
 * @param T
 */
interface Callback<T> {
    fun onComplete(result: T)
    fun onException(e: Exception?)
}

/**
 * Generic function to wrap callbacks with coroutines
 *
 *  ---> by <b>rwhite226</b> <--- thanks man!!!
 *
 * Check link for discussion and examples
 *
 * https://discuss.kotlinlang.org/t/generic-function-to-wrap-callbacks-with-coroutines/8885/2
 *
 *
 * Example:
 *
 * GlobalScope.launch(Dispatchers.IO) {
 *
 *    val firstPromise = async { awaitCallback<List<FirstResult>> { firstOperation(input, it) } }
 *    val secondPromise = async { awaitCallback<List<SecondResult>> { secondOperation(input, context, true, it) } }
 *
 *    val firstResult = firstPromise.await()
 *    val secondResult = secondPromise.await()
 *
 * }
 *
 *
 *
 * @param block Function1<Callback<T>, Unit>
 * @return T
 */
suspend fun <T> awaitCallback(block: (Callback<T>) -> Unit): T =
    suspendCancellableCoroutine { cont ->
        block(object : Callback<T> {
            override fun onComplete(result: T) = cont.resume(result)
            override fun onException(e: Exception?) {
                e?.let { cont.resumeWithException(it) }
            }
        })
    }


/**
 * Variation of [awaitCallback] with generic typealias
 * @param block Function1<Function1<T, Unit>, Unit>
 * @return T
 */
suspend fun <T> awaitResponse(block: ((T) -> Unit) -> Unit): T =
    suspendCancellableCoroutine { cont ->
        block(object : (T) -> Unit {
            override fun invoke(p1: T) {
                cont.resume(p1)
            }
        })
    }
