package com.sudzhaev.parzher

internal class InvalidFilterException(message: String) : Exception() {
    override val message: String = message
        get() = field.replace("\n", " ")

}
