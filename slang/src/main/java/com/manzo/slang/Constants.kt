package com.manzo.slang

/**
 * Created by Manolo D'Antonio on 29/08/2019
 */

/**
 * Matches literal unicode characters
 */
const val REGEX_UNICODE_CHARACTER = "\\p{L}"

/**
 * Matches unicode text including numbers and punctuation
 */
const val REGEX_UNICODE_TEXT = "[\\p{L} \\d!?\"',.;:()]"

/**
 * Matches special characters including punctuation
 */
const val REGEX_SPECIAL_CHAR = "[^\\p{L} \\d]"

