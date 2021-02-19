package com.manzo.slang.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * Compare the values of the objects of 2 lists
 */
infix fun <T> Collection<T>.isEqualValues(collection: Collection<T>) =
    collection.let { this.size == it.size && this.containsAll(it) }

/**
 * Compare the values of the objects of 2 lists
 */
infix fun <T> Collection<T>.isNotEqualValues(collection: Collection<T>) =
    !(this isEqualValues collection)

/**
 * Add an object to a list if not already present, null or blank
 * @receiver OB any object
 * @param targetList MutableList<OB>
 * @return Boolean result of the operation
 */
fun <OB> OB.addIfUnique(targetList: MutableList<OB>): Boolean {
    when (this) {
        null -> return false
        is String -> if (isBlank()) return false
    }

    return if (!targetList.contains(this)) targetList.add(this) else false
}

/**
 * Add an object to a list if not already present, null or blank
 * @receiver List<OB>
 * @param item OB any object
 * @return Boolean result of the operation
 */
fun <OB> List<OB>.addIfUnique(item: OB): Boolean {
    return item.addIfUnique(asMutable())
}

/**
 * Cast list to mutable list, or returns new empty list if original list is empty (list cannot be cast to mutable if empty)
 * @receiver List<T>
 * @return MutableList<T>
 */
fun <T> List<T>.asSafeMutable(): MutableList<T> {
    return (if (isNotEmpty()) this as MutableList else mutableListOf())
}

/**
 * Cast list to mutable list
 * @receiver List<T>
 * @return MutableList<T>
 */
fun <T> List<T>.asMutable() = this as MutableList<T>

/**
 * Replaces with the provided newValue every object in the list that matches the conditionBlock
 * @receiver MutableList<T>
 * @param newValue T
 * @param conditionBlock Function1<[@kotlin.ParameterName] T, Boolean>
 */
fun <T> MutableList<T>.replace(newValue: T, conditionBlock: (listElement: T) -> Boolean) {
    forEachIndexed { index, element ->
        if (conditionBlock(element)) add(index, newValue)
    }
}

/**
 * Removes the first element that matches the condition block.
 *
 * @receiver MutableList<T>
 * @param conditionBlock Function1<T, Boolean>
 * @return True if condition is found and removal is successful.
 */
fun <T> MutableCollection<T>.removeFirst(conditionBlock: (listElement: T) -> Boolean): Boolean {
    return find { conditionBlock(it) }?.let { remove(it) } ?: false
}

/**
 * Removes from the list all the elements that match the conditionBlock
 * Pre api24 compatible
 *
 * @receiver MutableList<T>
 * @param conditionBlock Function1<T, Boolean>
 */
fun <T> MutableCollection<T>.remove(conditionBlock: (listElement: T) -> Boolean) {
    iterator().let { iterator ->
        iterator.forEach { if (conditionBlock(it)) iterator.remove() }
    }
}


/**
 * Returns a list containing the results of applying the given [transform] function to each element in the original list.
 * The list is splitted in chunks so that multiple suspend [transform]s can be executed in parallel using [async].
 *
 *
 * --- example ---
 *
 * val pingResults = ipAddresses.mapAsync(coroutineScope) { pingAddress(it) }
 *
 *
 * @receiver List<T>
 * @param scope C - a [CoroutineScope]
 * @param chunkSize - Int size of the chunks
 * @param transform - the operation to perform
 * @return List<R>
 */
suspend fun <T, R, C : CoroutineScope> Collection<T>.mapAsync(
    scope: C,
    chunkSize: Int = 4,
    transform: suspend (element: T) -> R
): Collection<R> {
    return withContext(scope.coroutineContext) {
        asSequence()
            .chunked(chunkSize)
            .map { sublist ->
                async {
                    sublist.map { transform(it) }
                }
            }
            .toList()
            .awaitAll()
            .flatten()
    }
}
