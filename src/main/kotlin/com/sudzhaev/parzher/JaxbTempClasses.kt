package com.sudzhaev.parzher

import javax.xml.bind.annotation.*

@XmlType(name = "element")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
class InnerClass() {
    var tagone: String? = null
    var tagtwo: TagTwo? = null

    @XmlType(name = "tagtwo")
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    class TagTwo() {
        @XmlAttribute(name = "attr")
        var attr: String? = null
        @XmlValue
        var value: String? = null

        override fun toString() = "TagTwo(attr=$attr, value=$value)"
    }

    override fun toString() = "InnerClass(tagone=$tagone, tagtwo=$tagtwo)"
}

fun innerClassOrNull(innerClass: InnerClass): InnerClass? {
    if (innerClass.tagone == null && innerClass.tagtwo == null) {
        return null
    }
    if (innerClass.tagtwo == null) {
        val tagTwo = InnerClass.TagTwo()
        tagTwo.attr = "default attr"
        tagTwo.value = "default value"
        innerClass.tagtwo = tagTwo
    }
    return innerClass
}
