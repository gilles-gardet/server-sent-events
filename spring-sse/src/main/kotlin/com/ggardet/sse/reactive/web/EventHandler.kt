package com.ggardet.sse.reactive.web

import com.ggardet.sse.common.model.CustomUser
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDateTime

@Component
class EventHandler {
    fun fetchUserRoles(): Mono<ServerResponse> =
        ServerResponse.ok()
            .contentType(MediaType.TEXT_EVENT_STREAM)
            .body(ReactiveSecurityContextHolder.getContext()
                .map { it.authentication }
                .map { it.principal as CustomUser }
                .flatMapMany { customUser ->
                    Flux.fromIterable(customUser.authorities)
                        .map { ServerSentEvent.builder(it).build() }
                }
                .delayElements(Duration.ofSeconds(1)), ServerSentEvent::class.java)

    fun fetchSessionTimeRemaining(ignoreRequest: ServerRequest): Mono<ServerResponse> =
        ReactiveSecurityContextHolder.getContext()
            .map { it.authentication.principal as CustomUser }
            .map {
                val timeout = it.authDate.plusSeconds(5)
                val delay = Duration.between(LocalDateTime.now(), timeout)
                val event = ServerSentEvent.builder("$timeout - done").build()
                Mono.just(event).delaySubscription(delay)
            }
            .flatMap { serverSentEvent ->
                ServerResponse.ok()
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(serverSentEvent, ServerSentEvent::class.java)
            }
}
