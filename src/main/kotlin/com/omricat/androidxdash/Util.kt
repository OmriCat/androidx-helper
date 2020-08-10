package com.omricat.androidxdash

import com.omricat.androidxdash.codegen.GroupPathTree

fun CharSequence.camelCase(): String {
    val words = split("-")
    val firstWord = words.firstOrNull()?.toLowerCase() ?: ""
    val restOfWords = words.drop(1).map { it.capitalize() }
    return firstWord + restOfWords.joinToString("")
}


