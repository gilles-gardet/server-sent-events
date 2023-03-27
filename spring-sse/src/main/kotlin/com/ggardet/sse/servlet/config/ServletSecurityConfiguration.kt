package com.ggardet.sse.servlet.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Profile("servlet")
@Configuration
@EnableWebSecurity
class ServletSecurityConfiguration {
    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain =
        httpSecurity.csrf().disable()
            .authorizeHttpRequests { exchanges -> exchanges
                .requestMatchers(HttpMethod.GET, "/sse/reactive/notifications").permitAll()
                .anyRequest().authenticated() }
            .httpBasic(Customizer.withDefaults())
            .formLogin(Customizer.withDefaults())
            .build()
}
