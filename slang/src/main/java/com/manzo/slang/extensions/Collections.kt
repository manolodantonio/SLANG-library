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


fun <T> List<T>.asSafeMutable(): MutableList<T> {
    return (if (isNotEmpty()) this as MutableList else mutableListOf())
}