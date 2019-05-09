package com.sudzhaev.parzher

typealias AttributeMatcher = (String?) -> Boolean

fun isEqualTo(value: String): AttributeMatcher = value::equals

fun isMoreThan(value: Int): AttributeMatcher = { attr ->
    val intAttr = attr?.toIntOrNull()
    intAttr != null && intAttr > value
}
