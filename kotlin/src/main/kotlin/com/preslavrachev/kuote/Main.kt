package com.preslavrachev.kuote

import com.mongodb.MongoClientURI
import com.mongodb.client.MongoCollection
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
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

@Location("/k/{id}")
class KuoteResource(val id: String)

fun initDependencyGraph() = Kodein {
    bind<MongoCollection<Kuote>>() with singleton {
        val uri = MongoClientURI(System.getenv("MONGODB_URI"))
        val kuotesCollection = KMongo.createClient(uri = uri)
                .getDatabase(uri.database!!)
                .getCollection<Kuote>("kuotes")

        kuotesCollection
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
            call.respond(PebbleContent("kuote.html", mapOf("content" to kuote.content, "source" to kuote.source)))
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

