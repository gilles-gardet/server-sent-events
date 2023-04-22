package com.ggardet.sse.authentication.listener

import mu.KotlinLogging
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.stereotype.Component

@Component
class AuthenticationEventListener : ApplicationListener<AuthenticationSuccessEvent> {
    private val log = KotlinLogging.logger {}
    override fun onApplicationEvent(authenticationSuccessEvent: AuthenticationSuccessEvent) {
        log.info { "Session created" }
    }
}
