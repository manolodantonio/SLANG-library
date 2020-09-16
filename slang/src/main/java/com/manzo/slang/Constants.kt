package com.manzo.slang

/**
 * Created by Manolo D'Antonio on 29/08/2019
 */

/**
 * Matches unicode letters excluding numbers and special characters
 * */
const val REGEX_UNICODE_CHARACTER = "[\\p{L}]"

/**
 * Matches unicode Uppercase letters excluding numbers and special characters
 * */
const val REGEX_UNICODE_UPPERCASE_CHARACTER = "[\\p{Lu}]"

/**
 * Matches unicode text including numbers and special characters
 */
const val REGEX_UNICODE_TEXT = "[^\\p{L} ]"

/**
 * Matches numbers
 */
const val REGEX_NUMBERS = "[\\d]"

/**
 * Matches special characters including punctuation
 */
const val REGEX_SPECIAL_CHAR = "[^\\p{L} \\d]"

