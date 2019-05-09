package com.sudzhaev.parzher

typealias AttributeMatcher = (String?) -> Boolean

fun isEqualTo(value: String): AttributeMatcher = value::equals

fun isMoreThan(value: Int): AttributeMatcher = matcher@{ attr ->
    val intAttr = attr?.toIntOrNull() ?: return@matcher false
    intAttr > value
}
