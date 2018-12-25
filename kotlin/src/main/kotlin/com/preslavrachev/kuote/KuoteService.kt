package com.preslavrachev.kuote

import com.algolia.search.Index
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Kuote(val objectID: String, val content: String)

class KuoteService(val index: Index<Kuote>) {
    fun retrieveKuote(id: String): Kuote {
        val kuote = index.getObject("nytimes.com:the_myth_of_quality_time_3cd46d1a")
        return kuote.orElseThrow { RuntimeException("The kuote with ID: $id is not in the storage!") }
    }
}