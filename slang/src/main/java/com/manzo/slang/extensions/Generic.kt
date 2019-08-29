package com.manzo.slang.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by Manolo D'Antonio on 19/07/2019
 */


/**
 * convert a data class to a map where values are strings
 */
fun <T> T.serializeToStringsMap(): Map<String, String> = convert()

/**
 * convert a data class to a map
 */
fun <T> T.serializeToMap(): Map<String, Any> = convert()

/***
 * convert a map to a data class
 */
inline fun <reified T> Map<String, Any>.toDataClass(): T = convert()

/**
 * convert an object of type I to type O via Gson.
 *
 * By ColonelCustard from https://stackoverflow.com/a/56347214/4473512
 */
inline fun <I, reified O> I.convert(): O = Gson().toJson(this).fromJson()


/**
 * Converts object to json via GSON
 * @receiver T
 * @return String
 */
fun <T> T.toJson(): String = Gson().toJson(this)


/**
 * Converts string to object via GSON type token
 * @receiver String
 * @return T
 */
inline fun <reified T> String.fromJson(): T =
    Gson().fromJson(this, object : TypeToken<T>() {}.type)

/**
 * Converts string to object via GSON
 * @receiver String
 * @param model Class<T>
 * @return T
 */
inline fun <reified T> String.fromJson(model: Class<T>): T = Gson().fromJson(this, model)


/**
 * Executes block only if receiver is not null.
 *
 * Example: myString ifNotNull { doMyStuff() }
 *
 * @receiver Any?
 * @param block Function0<Unit>
 */
infix fun Any?.ifNotNull(block: () -> Unit) {
    this?.let { block.invoke() }
}

/**
 * Short for [ifNotNull]
 * @receiver Any?
 * @param block Function0<Unit>
 */
infix fun Any?.nn(block: () -> Unit) = this ifNotNull block