package com.manzo.slang.extensions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

/**
 * Use to notify observers of LiveData lists
 */
fun <T> MutableLiveData<MutableList<T>>.add(item: T) {
    value = value?.apply { add(item) }
}

/**
 * Use to notify observers of LiveData lists
 */
fun <T> MutableLiveData<MutableList<T>>.replace(index: Int, item: T) {
    value = value?.apply { set(index, item) }
}

/**
 * Use to notify observers of LiveData lists
 */
fun <T> MutableLiveData<MutableList<T>>.remove(item: T) {
    value = value?.apply { remove(item) }
}

/**
 * Use to notify observers of LiveData
 *
 *  * !!!This is your last resource!!!
 *
 * If you are using this, something could be done better!
 */
fun <T> MutableLiveData<T>.triggerObservation() {
    value = value
}

/**
 * Remove observer after first observation
 */
fun <T> LiveData<T>.observeOnce(onObserved: (T?) -> Unit) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            removeObserver(this)
            onObserved.invoke(t)
        }
    })
}

/**
 *  MutableMap getOrDefault String for Api version  < 24
 */
fun MutableMap<String, String>.getOrDefaultValue(key: String, default: String): String? {
    if (this.containsKey(key))
        return this[key]
    return default
}