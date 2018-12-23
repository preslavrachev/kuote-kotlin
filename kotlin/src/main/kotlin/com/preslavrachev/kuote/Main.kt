package com.preslavrachev.kuote

import com.tlogx.ktor.pebble.Pebble
import com.tlogx.ktor.pebble.PebbleContent
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {
        install(Pebble) {
            templateDir = "templates/"
        }

        routing {
            get("/") {
                call.respond(PebbleContent("index.html", mapOf()))
            }
        }
    }.start(wait = true)
}