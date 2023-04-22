package com.ggardet.sse.authentication.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CsrfTokenRepository
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity(debug = false)
class ServletSecurityConfiguration(val sessionRegistry: SessionRegistry, val csrfTokenRepository: CsrfTokenRepository) {
    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .cors { it.configurationSource { CorsConfiguration().applyPermitDefaultValues() } }
            .csrf { it.csrfTokenRepository(csrfTokenRepository) }
            .authorizeHttpRequests {
                it.mvcMatchers(HttpMethod.GET, "/notifications", "/expired", "/invalid")
                    .permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement {
                it.maximumSessions(1).sessionRegistry(sessionRegistry)
            }
            .httpBasic(Customizer.withDefaults())
            .logout {
                it.invalidateHttpSession(true)
                    .deleteCookies("SESSION-ID")
                    .permitAll()
            }
        return httpSecurity.build()
    }
}
