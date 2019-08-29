package com.manzo.slang.extensions

import java.io.InputStream

/**
 * Created by Manolo D'Antonio on 29/08/2019
 */


/**
 * Reads the inputStream as string
 * @receiver InputStream
 * @param maxLines max lines to read from the beginning
 * @return String
 */
fun InputStream.text(maxLines: Int = 0): String {
    var result = ""
    bufferedReader().useLines { lines ->
        lines.forEachIndexed { position, line ->
            if (maxLines in 1..position) {
                return@forEachIndexed
            }

            if (position != 0) result += "\n"
            result += line

        }
    }
    return result
}
