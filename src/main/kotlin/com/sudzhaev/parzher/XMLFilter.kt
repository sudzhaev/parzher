package com.sudzhaev.parzher

data class Attribute(val name: String, val value: String)

data class Tag(val name: String, val attributes: List<Attribute>, val extract: List<String>)

data class XMLFilter(val tag: Tag, val nestedFilters: List<XMLFilter>) {

    fun reverse(): Map<Tag, Tag?> {
        val map = HashMap<Tag, Tag?>().apply {
            put(tag, null)
        }
        nestedFilters.forEach {
            map.putAll(it.reverse(tag))
        }
        return map
    }

    private fun reverse(parentTag: Tag): Map<Tag, Tag?> {
        val map = HashMap<Tag, Tag?>().apply {
            put(tag, parentTag)
        }
        nestedFilters.forEach {
            map.putAll(it.reverse(tag))
        }
        return map
    }
}
