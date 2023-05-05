package com.ggardet.sse.event.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggardet.sse.event.domain.Notification
import com.ggardet.sse.authentication.domain.CustomUser
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletResponse

private const val X_ACCEL_BUFFERING = "X-Accel-Buffering"

@PreAuthorize("hasRole('USER')")
@RestController
@RequestMapping
class EventController {
    private val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val emitters = ConcurrentHashMap<String, MutableList<SseEmitter>>()
    private val objectMapper: ObjectMapper = ObjectMapper()
    private val log = KotlinLogging.logger {}

    @GetMapping(value = ["/sse"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun fetchSessionTimeRemaining(authentication: Authentication, httpServletResponse: HttpServletResponse): SseEmitter? {
        val customUser = authentication.principal as CustomUser
        val timeout: LocalDateTime = customUser.authDate.plusSeconds(5)
        val delay: Long = Duration.between(LocalDateTime.now(), timeout).seconds;
        val emitter = SseEmitter()
        val firstCommand = fireSessionEvent(customUser, timeout, emitter)
        executorService.schedule(firstCommand, delay, TimeUnit.SECONDS)
        val lastCommand = fireSessionEvent(customUser, timeout, emitter, true)
        executorService.schedule(lastCommand, delay + 5, TimeUnit.SECONDS)
        httpServletResponse.setHeader(HttpHeaders.ACCEPT_ENCODING, "")
        httpServletResponse.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
        httpServletResponse.setHeader(X_ACCEL_BUFFERING, "no")
        return emitter
    }

    private fun fireSessionEvent(
        customUser: CustomUser,
        timeout: LocalDateTime,
        emitter: SseEmitter,
        shouldComplete: Boolean = false,
    ): () -> Unit = {
        val type = if (shouldComplete) "complete" else "message"
        val event = event().id(customUser.username).name(type).data("$timeout - $type")
        emitter.send(event)
        if (shouldComplete) {
            emitter.complete()
        }
    }

    @GetMapping("/notifications", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun listenNotifications(@RequestParam eventId: String, httpServletResponse: HttpServletResponse): SseEmitter? {
        val emitter = SseEmitter(Duration.ofHours(8).toMillis())
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
        emitters.computeIfAbsent(eventId) { mutableListOf() }.add(emitter)
        httpServletResponse.setHeader(HttpHeaders.ACCEPT_ENCODING, "")
        httpServletResponse.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
        httpServletResponse.setHeader(X_ACCEL_BUFFERING, "no")
        return emitter
    }

    @PostMapping("/notifications")
    @ResponseStatus(NO_CONTENT)
    fun fireNotification(
        @RequestParam eventId: String,
        @RequestBody notification: Notification
    ) = emitters[eventId]?.forEach { handleEmitter(notification, it) }

    private fun handleEmitter(
        notification: Notification,
        sseEmitter: SseEmitter,
    ) = try {
        val data = objectMapper.writeValueAsString(notification)
        val sseEventBuilder = event().data(data)
        sseEmitter.send(sseEventBuilder)
    } catch (ioException: IOException) {
        log.error("Send SSE exception", ioException)
    }
}
