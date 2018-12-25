package com.preslavrachev.kuote

data class Kuote(val content: String)

class KuoteService {
    fun retrieveKuote(id: String): Kuote {
        return Kuote(content = "Hello World")
    }
}