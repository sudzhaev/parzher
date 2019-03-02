package com.sudzhaev.parzher

import java.io.FileInputStream
import javax.xml.stream.XMLInputFactory

data class ValueDto(val param: String, val size: String, val num: Int)

fun main() {
    val xmlInputFactory = XMLInputFactory.newInstance()
    val xmlEventReader = xmlInputFactory.createXMLEventReader(FileInputStream("src/main/resources/somexml.xml"))
    val filters = buildFilterDsl()
    val xmlEventParser = XmlEventParser(filters)
    val dataExtractor = DataExtractor(xmlEventReader, xmlEventParser)
    val objectExtractor = ObjectExtractor(ValueDto::class.java)
    var attrs = dataExtractor.next()
    while (attrs != null) {
        println(objectExtractor.get(attrs))
        attrs = dataExtractor.next()
    }
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
                    attribute("num") { it?.toInt() ?: 0 }
                }
                nested {
                    tag("element") {
                        terminate = true
                        attributes {
                            attribute(name = "size", value = "s")
                        }
                        extract {
                            attribute("size")
                        }
                    }
                }
            }
        }
    }
}
