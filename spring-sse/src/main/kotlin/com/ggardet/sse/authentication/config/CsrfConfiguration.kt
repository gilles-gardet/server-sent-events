package com.ggardet.sse.authentication.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfTokenRepository


@Configuration
internal class CsrfConfiguration {
    @Bean
    fun csrfTokenRepository(): CsrfTokenRepository? {
        val cookieCsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
        cookieCsrfTokenRepository.cookiePath = "/"
        return cookieCsrfTokenRepository
    }
}
