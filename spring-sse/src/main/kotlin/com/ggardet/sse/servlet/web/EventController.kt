package com.ggardet.sse.servlet.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggardet.sse.common.model.CustomUser
import com.ggardet.sse.common.model.Notification
import mu.KotlinLogging
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event
import java.io.IOException
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/sse/servlet")
class EventController {
    private val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val emitters = mutableMapOf<String, SseEmitter>()
    private val objectMapper: ObjectMapper = ObjectMapper()
    private val log = KotlinLogging.logger {}

    @GetMapping(value = ["/session"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun fetchSessionTimeRemaining(authentication: Authentication): SseEmitter? {
        val user: CustomUser = authentication.principal as CustomUser
        val timeout: LocalDateTime = user.authDate.plusSeconds(5)
        val delay: Long = Duration.between(LocalDateTime.now(), timeout).seconds;
        val emitter = SseEmitter()
        val command = {
            val event = event().id(user.email).data("$timeout - done")
            emitter.send(event)
            emitter.complete()
        }
        executorService.schedule(command, delay, TimeUnit.SECONDS)
        return emitter
    }

    @GetMapping("/notifications")
    fun listenNotifications(@RequestParam eventId: String): SseEmitter? {
        val emitter = SseEmitter(TimeUnit.MINUTES.toMillis(10))
        emitter.onCompletion {
            log.info("SSE connection closed")
            emitters.remove(eventId)
        }
        emitter.onTimeout {
            log.info("SSE connection timed out")
            emitters.remove(eventId)
            emitter.complete()
        }
        emitter.onError { throwable: Throwable? ->
            log.error("Listen SSE exception", throwable)
            emitters.remove(eventId)
        }
        emitters[eventId] = emitter
        return emitter
    }

    @PostMapping("/notifications")
    @ResponseStatus(ACCEPTED)
    fun fireNotification(
        @RequestParam eventId: String,
        @RequestBody notification: Notification
    ) {
        emitters[eventId]?.let { handleEmitter(notification, it, eventId) }
    }

    private fun handleEmitter(
        notification: Notification,
        sseEmitter: SseEmitter,
        eventId: String
    ) = try {
        val data = objectMapper.writeValueAsString(notification)
        val sseEventBuilder = event().name("message").data(data)
        sseEmitter.send(sseEventBuilder)
    } catch (ioException: IOException) {
        log.error("Send SSE exception", ioException)
        emitters.remove(eventId)
    }
}
