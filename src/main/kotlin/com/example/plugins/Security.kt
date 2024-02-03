package com.example.plugins

import com.example.session.ChatSession
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<ChatSession>("SESSION")
    }

    intercept(ApplicationCallPipeline.Features){
        if(call.sessions.get<ChatSession>() == null){
            val userName = call.parameters["username"] ?: "Guest"
            call.sessions.set(ChatSession(userName, generateNonce()))
        }
    }
}
