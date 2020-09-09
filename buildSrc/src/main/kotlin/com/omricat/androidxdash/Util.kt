package com.omricat.androidxdash

fun CharSequence.camelCase(delimiter: Char = '-'): String {
    val words = split(delimiter)
    val firstWord = words.firstOrNull()?.toLowerCase() ?: ""
    val restOfWords = words.drop(1).map { it.capitalize() }
    return firstWord + restOfWords.joinToString("")
}


