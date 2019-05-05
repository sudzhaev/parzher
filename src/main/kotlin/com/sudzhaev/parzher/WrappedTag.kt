package com.sudzhaev.parzher

sealed class WrappedTag
data class StartTag(val tag: Tag) : WrappedTag()
object EndTag : WrappedTag()
