package com.ggardet.sse.reactive.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Profile("reactive")
@Configuration
@EnableWebFluxSecurity
class ReactiveSecurityConfiguration {
    @Bean
    fun filterChain(serverHttpSecurity: ServerHttpSecurity): SecurityWebFilterChain =
        serverHttpSecurity
            .csrf().disable()
            .authorizeExchange { exchanges -> exchanges
                    .pathMatchers("/sse/servlet/notifications").permitAll()
                    .anyExchange().authenticated()
            }
            .httpBasic(Customizer.withDefaults())
            .formLogin(Customizer.withDefaults())
            .build()
}
