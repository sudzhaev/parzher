package com.sudzhaev.parzher

import java.io.FileInputStream
import javax.xml.bind.JAXBContext
import javax.xml.bind.Unmarshaller
import javax.xml.stream.XMLInputFactory

data class ValueDto(val param: String, val innerClass: InnerClass?, val n: Int?)

val unmarshaller: Unmarshaller = JAXBContext.newInstance(InnerClass::class.java).createUnmarshaller()

fun main() {
    val xmlInputFactory = XMLInputFactory.newInstance()
    val xmlEventReader = xmlInputFactory.createXMLEventReader(FileInputStream("src/main/resources/somexml.xml"))
    val filters = buildFilterDsl()
    val parzher = Parzher(ValueDto::class.java, xmlEventReader, filters)
    parzher.forEach { println(it) }
}

fun buildFilterDsl() = filters {
    tag("div") {
        attributes {
            attribute(name = "class", value = "name")
        }
        nested {
            tag("elements") {
                attributes {
                    attribute(name = "val", value = "2")
                }
                extract {
                    attribute("param")
                    attribute("num", propertyName = "n") { it?.toInt() }
                }
                nested {
                    tag("element") {
                        terminate()
                        unmarhsal(InnerClass::class.java, unmarshaller, resultHandler = ::innerClassOrNull)
                        attributes {
                            attribute(name = "size", value = "s")
                        }
                    }
                }
            }
        }
    }
}
