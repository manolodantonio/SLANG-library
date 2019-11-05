package com.manzo.slang.extensions

import kotlinx.coroutines.*
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


/**
 *
 * Creates new coroutine and returns its future result as a Deferred.
 *
 * Chain with [await] or [awaitNet]
 *
 * Example:
 * ```
 * async {
 *  longOperation()
 * } await { result ->
 *  updateUi(result)
 * }
 *
 * ```
 *
 * @param block Function0<T>
 * @return last line as a Deferred. [await] can be appended to retrieve an async result.
 */
fun <T> defer(block: () -> T): Deferred<T> {
    return GlobalScope.async {
        block.invoke()
    }
}


/**
 * Awaits in a background thread and sends the result in the main thread.
 * This is an appropriate choice for compute-intensive operations that consume CPU resources.
 *
 * For blocking IO operations (Network, File..), use [awaitNet]
 *
 *
 * Example:
 * ```
 * async {
 *  longOperation()
 * } await { result ->
 *  updateUi(result)
 * }
 *
 * ```
 * @receiver Deferred<T>
 * @param result Function1<[@kotlin.ParameterName] T, Unit>
 */
infix fun <T> Deferred<T>.await(result: (result: T) -> Unit) {
    GlobalScope.launch {
        val value = await()
        launch(Dispatchers.Main) {
            result.invoke(value)
        }
    }
}


/**
 * Awaits in a background thread and sends the result in the main thread.
 * Designed for IO-intensive blocking operations (like file I/O and blocking socket I/O).
 *
 * For compute-intensive operations use [await]
 *
 *
 * Example:
 * ```
 * async {
 *  longOperation()
 * } await { result ->
 *  updateUi(result)
 * }
 *
 * ```
 * @receiver Deferred<T>
 * @param result Function1<[@kotlin.ParameterName] T, Unit>
 */
infix fun <T> Deferred<T>.awaitNet(result: (result: T) -> Unit) {
    GlobalScope.launch(Dispatchers.IO) {
        val value = await()
        launch(Dispatchers.Main) {
            result.invoke(value)
        }
    }
}


/**
 * Awaits in a background thread and sends the result in the main thread.
 * This is an appropriate choice for compute-intensive operations that consume CPU resources.
 *
 * For blocking IO operations (Network, File..), use [(() -> T).awaitNet]
 *
 *
 * Example:
 * ```
 * // we have a fun longOperation()
 * ::longOperation.await { result ->
 *  updateUi(result)
 * }
 *
 * ```
 * @receiver Deferred<T>
 * @param result Function1<[@kotlin.ParameterName] T, Unit>
 */
infix fun <T> (() -> T).await(result: (result: T) -> Unit) {
    GlobalScope.launch {
        val value = invoke()
        launch(Dispatchers.Main) {
            result.invoke(value)
        }
    }
}


/**
 * Awaits in a background thread and sends the result in the main thread.
 * Designed for IO-intensive blocking operations (like file I/O and blocking socket I/O).
 *
 * For compute-intensive operations use [(() -> T).await]
 *
 *
 * Example:
 * ```
 * // we have a fun longOperation()
 * ::longOperation.awaitNet { result ->
 *  updateUi(result)
 * }
 *
 * ```
 * @receiver Deferred<T>
 * @param result Function1<[@kotlin.ParameterName] T, Unit>
 */
infix fun <T> (() -> T).awaitNet(result: (result: T) -> Unit) {
    GlobalScope.launch(Dispatchers.IO) {
        val value = invoke()
        launch(Dispatchers.Main) {
            result.invoke(value)
        }
    }

}


