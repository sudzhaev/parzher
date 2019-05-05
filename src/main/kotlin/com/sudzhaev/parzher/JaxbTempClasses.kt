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

        override fun toString(): String {
            return "TagTwo(attr=$attr, value=$value)"
        }
    }

    override fun toString(): String {
        return "InnerClass(tagone=$tagone, tagtwo=$tagtwo)"
    }
}
