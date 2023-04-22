package com.ggardet.sse.authentication.listener

import mu.KotlinLogging
import org.springframework.context.ApplicationListener
import org.springframework.security.core.session.SessionDestroyedEvent
import org.springframework.stereotype.Component

@Component
class SessionDestroyedEventListener : ApplicationListener<SessionDestroyedEvent> {
    private val log = KotlinLogging.logger {}
    override fun onApplicationEvent(sessionDestroyedEvent: SessionDestroyedEvent) {
        log.info { "Session destroyed" }
    }
}
