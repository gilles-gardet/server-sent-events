package com.ggardet.sse.reactive.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class EventRouter {
    @Bean
    fun route(eventHandler: EventHandler): RouterFunction<ServerResponse> {
        val sseRouter = RouterFunctions.route()
            .GET("/authorities") { eventHandler.fetchUserRoles() }
            .GET("/session", eventHandler::fetchSessionTimeRemaining)
            .build()
        return RouterFunctions.nest(RequestPredicates.path("/sse/reactive"), sseRouter)
    }
}

