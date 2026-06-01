package com.example

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(
        arrayOf(*args, "-P:ktor.deployment.host=0.0.0.0")
    )
}

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureDatabases()
    configureRouting()
}
