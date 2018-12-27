package com.preslavrachev.kuote

import com.algolia.search.ApacheAPIClientBuilder
import com.algolia.search.Index
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.tlogx.ktor.pebble.Pebble
import com.tlogx.ktor.pebble.PebbleContent
import io.ktor.application.Application
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
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

@Location("/k/{id}")
class KuoteResource(val id: String)

fun initDependencyGraph() = Kodein {
    bind<Index<Kuote>>() with singleton {
        val objectMapper = ObjectMapper().registerModule(KotlinModule())

        val apiClient = ApacheAPIClientBuilder(System.getenv("ALGOLIA_API_ID"), System.getenv("ALGOLIA_API_KEY"))
                .setObjectMapper(objectMapper)
                .build()
        apiClient.initIndex("dev_kuote", Kuote::class.java)
    }
    bind<KuoteService>() with singleton { KuoteService(instance()) }
}

fun Application.routes() = routes(initDependencyGraph())

fun Application.routes(injector: Kodein) {
    val kuoteService by injector.instance<KuoteService>()

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

        get<KuoteResource> {
            val kuote = kuoteService.retrieveKuote(it.id)
            call.respond(PebbleContent("kuote.html", mapOf("content" to kuote.content)))
        }
    }
}

@KtorExperimentalLocationsAPI
fun main(args: Array<String>) {
    embeddedServer(
            factory = Netty,
            port = System.getenv("PORT")?.toInt() ?: 8080,
            module = Application::routes
    ).start(wait = true)
}

