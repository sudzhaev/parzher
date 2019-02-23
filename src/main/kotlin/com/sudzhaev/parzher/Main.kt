package com.sudzhaev.parzher

import java.io.FileInputStream
import javax.xml.stream.XMLInputFactory

fun main() {
    val xmlInputFactory = XMLInputFactory.newInstance()
    val xmlEventReader = xmlInputFactory.createXMLEventReader(FileInputStream("src/main/resources/somexml.xml"))
    val filters = buildFilterDsl()
    val reader = FilteredXMLExtractReader(xmlEventReader, filters)
    while (reader.hasNext()) {
        val event = reader.nextEvent()
        println(event)
    }
}

fun buildFilterDsl() = filters {
    tag {
        name = "div"
        attributes {
            attribute {
                name = "class"
                value = "name"
            }
        }
        nested {
            tag {
                name = "elements"
                attributes {
                    attribute {
                        name = "val"
                        value = "2"
                    }
                }
                extract {
                    attribute("param")
                }
                nested {
                    tag {
                        name = "element"
                        attributes {
                            attribute {
                                name = "size"
                                value = "s"
                            }
                        }
                    }
                }
            }
        }
    }
}
