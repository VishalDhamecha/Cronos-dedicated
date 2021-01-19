package com.company.mysapcpsdkprojectoffline.util.extension

import java.util.regex.Matcher
import java.util.regex.Pattern

fun String.Companion.empty(): String {
    return ""
}

fun String.Companion.isEmpty(text: Any?): Boolean {
    return text == null || text.toString().trim() == String.empty()
}

fun String.Companion.isNotEmpty(text: Any?): Boolean {
    return text != null && text.toString().trim() != String.empty()
}

fun String.capitalizeEveryFirstLatter(): String {
    val capBuffer = StringBuffer()
    val capMatcher: Matcher =
        Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(this)
    while (capMatcher.find()) {
        capMatcher.appendReplacement(
            capBuffer,
            capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase()
        )
    }
    return capMatcher.appendTail(capBuffer).toString()
}

