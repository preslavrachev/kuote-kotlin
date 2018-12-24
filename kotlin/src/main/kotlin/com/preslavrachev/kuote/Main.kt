package com.preslavrachev.kuote

import com.tlogx.ktor.pebble.Pebble
import com.tlogx.ktor.pebble.PebbleContent
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

@Location("/k/{id}")
class KuoteResource(val id: String)

@KtorExperimentalLocationsAPI
fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {

        install(Locations)

        install(Pebble) {
            templateDir = "templates/"
        }

        routing {
            static("assets") {
                resources("assets")
            }

            get("/") {
                call.respond(PebbleContent("index.html", mapOf()))
            }
        }
    }.start(wait = true)
}