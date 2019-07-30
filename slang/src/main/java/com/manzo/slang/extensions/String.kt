package com.manzo.slang.extensions

import android.content.Context
import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.UnsupportedEncodingException


/**
 * Replace all the targets with the provided replacement
 */
fun String.replace(targets: List<String>, replacement: String, ignoreCase: Boolean = false): String {
    var eval = this
    for (v in targets) eval = eval.replace(v, replacement, ignoreCase)
    return eval
}

/**
 * Remove targets from the string
 */
fun String.remove(vararg targets: String, ignoreCase: Boolean = false): String {
    return replace(targets.toList(), "", ignoreCase)
}

/**
 * Remove target regex matches from the string
 */
fun String.remove(target: Regex): String {
    return replace(target, "")
}

/**
 *Check if string contains one or more of the queries.
 *@param mustContainAll if true, check if contains ALL the queries.
 */
fun String.contains(vararg queries: String, ignoreCase: Boolean = false, mustContainAll: Boolean = false): Boolean {
    if (mustContainAll) {
        queries.forEach { query ->
            contains(query as CharSequence, ignoreCase).takeIf { it } ?: return false
        }
        return true
    } else {
        findAnyOf(queries.toList(), ignoreCase = ignoreCase) ?: return false
        return true
    }
}

/**
 * Encode String to base64
 */
fun String.toBase64(): String {
    return try {
        toByteArray(charset("UTF-8")).run {
            Base64.encodeToString(this, Base64.NO_WRAP)
        }
    } catch (e1: UnsupportedEncodingException) {
        e1.logError()
        this
    }
}

/**
 * Decode String from base64
 */
fun String.fromBase64(): String {
    return try {
        String(Base64.decode(this, Base64.DEFAULT))
    } catch (e1: UnsupportedEncodingException) {
        e1.logError()
        this
    }
}


/**
 * Extension function for negating isNullOrBlank
 */
fun String?.isNotNullOrBlank() = !isNullOrBlank()

/**
 * Extension function for negating isNullOrBlank
 */
fun CharSequence?.isNotNullOrBlank() = !isNullOrBlank()

/**
 * Write string to a target file. Creates the file if needed.
 */
fun String.writeToInternalFile(context: Context, filename: String) =
    context.getInternalFile(filename)
        .apply {
            writeText(this@writeToInternalFile)
        }


/**
 * Extension method for String types for capitalizing first letter in every word
 */
fun String.capitalizeEveryFirstLetter(): String {
    var output = ""

    for (word in this.toLowerCase().split(" ").toMutableList()) {
        output += if (output == "") word.capitalize() else " " + word.capitalize()
    }
    return output
}


/**
 * Truncates the string to the specified length number of characters
 */
fun String.truncateTo(len: Int): String {
    return if (this.length > len) {
        this.substring(len) + "…"
    } else {
        this
    }
}


/**
 * Convert a string to Json and output pretty print
 * @receiver String
 * @param indentSpaces Int
 * @return String?
 */
fun String.jsonPrettyPrint(indentSpaces: Int = 2): String {
    return try {
        JSONTokener(this).nextValue().let { token ->
            when (token) {
                is JSONObject -> JSONObject(this).toString(indentSpaces)
                else -> JSONArray(this).toString(indentSpaces)
            }
        }
    } catch (e: Exception) {
        e.logError()
        this
    }
}


/**
 * Returns the first mac address in the string, or empty.
 * @receiver String
 * @return String
 */
fun String.findMAC() = REGEX_MAC_ADDRESS.toRegex().find(this)?.value ?: ""

/**
 * Returns the first mac address in the string, or null.
 * @receiver String
 * @return String
 */
fun String.findMACOrNull() = REGEX_MAC_ADDRESS.toRegex().find(this)?.value

/**
 * Used in [String.findMAC]
 */
const val REGEX_MAC_ADDRESS = "([\\da-fA-f]{2}[:-]){5}[\\da-fA-f]{2}"

