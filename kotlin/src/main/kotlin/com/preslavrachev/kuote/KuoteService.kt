package com.preslavrachev.kuote

import com.fasterxml.jackson.annotation.JsonInclude
import com.mongodb.client.MongoCollection
import org.litote.kmongo.eq
import org.litote.kmongo.findOne

@JsonInclude(JsonInclude.Include.NON_NULL)
data class KuoteSource(val title: String, val subtitle: String, val image: String)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Kuote(val content: String, val slug: String, val source: KuoteSource)

class KuoteService(val kuotesCollection: MongoCollection<Kuote>) {
    fun retrieveKuote(id: String): Kuote {
        val kuote = kuotesCollection.findOne(Kuote::slug eq  "nytimes.com:the_myth_of_quality_time_3cd46d1a")
        return kuote!!
    }
}