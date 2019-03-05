package com.sudzhaev.parzher

import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.specs.StringSpec
import java.io.FileInputStream
import javax.xml.stream.XMLInputFactory

data class Div(val classAttr: String)

class ParzherTest : StringSpec({

    fun createDivParzher(file: String, filters: List<XMLFilter>): Parzher<Div> {
        val xmlInputFactory = XMLInputFactory.newInstance()
        val xmlEventReader = xmlInputFactory.createXMLEventReader(FileInputStream(file))
        return Parzher(Div::class.java, xmlEventReader, filters)
    }

    "extract divs" {
        val givenFilters = filters {
            tag("div") {
                terminate()
                extract {
                    attribute("class", newName = "classAttr")
                }
            }
        }
        val parzher = createDivParzher("src/test/resources/somexml.xml", givenFilters)
        val divs = parzher.toList()
        divs shouldHaveSize 3
        divs shouldContainAll listOf(Div("name"), Div("gogo"))
    }
})
