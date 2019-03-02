package com.sudzhaev.parzher

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class ObjectExtractor<T>(private val clazz: Class<T>) {

    private val objectMapper = ObjectMapper().registerKotlinModule()

    fun get(attributes: Map<String, Any?>): T = objectMapper.convertValue(attributes, clazz)
}
