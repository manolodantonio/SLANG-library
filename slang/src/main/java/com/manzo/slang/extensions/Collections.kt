package com.manzo.slang.extensions

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
 * Extension function to add an object to a list if not already present
 */
fun <OB> OB.addIfUnique(targerList: MutableList<OB>): Boolean {
    when (this) {
        null -> return false
        is String -> if (this.isBlank()) return false
    }

    return if (!targerList.contains(this)) targerList.add(this) else false
}

/**
 * Extension function to add an object to a list if not already present
 */
fun <OB> List<OB>.addIfUnique(item: OB): Boolean {
    return item.addIfUnique(this as MutableList<OB>)
}

/**
 * Cast list to mutable list, or returns new empty list if original list is empty (list cannot be cast to mutable if empty)
 * @receiver List<T>
 * @return MutableList<T>
 */
fun <T> List<T>.asSafeMutable(): MutableList<T> {
    //todo see if empty list can be cast to mutable specifying the type -> this as MutableList<T>
    return (if (isNotEmpty()) this as MutableList else mutableListOf())
}

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
fun <T> MutableList<T>.removeFirst(conditionBlock: (listElement: T) -> Boolean): Boolean {
    return find { conditionBlock.invoke(it) }?.let { remove(it) } ?: false
}

/**
 * Removes from the list all the elements that match the conditionBlock
 *
 * @receiver MutableList<T>
 * @param conditionBlock Function1<T, Boolean>
 */
fun <T> MutableList<T>.removeIf(conditionBlock: (listElement: T) -> Boolean) {
    forEach { if (conditionBlock(it)) remove(it) }
}

