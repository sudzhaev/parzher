package com.sudzhaev.parzher

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class ObjectExtractor<T>(private val clazz: Class<T>) {

    private val objectMapper = ObjectMapper().registerKotlinModule()

    fun convert(map: Map<String, String?>): T = objectMapper.convertValue(map, clazz)
}
