package com.ggardet.sse.authentication.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.web.session.HttpSessionEventPublisher
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession
import org.springframework.session.web.http.CookieSerializer
import org.springframework.session.web.http.DefaultCookieSerializer

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 10)
internal class HttpSessionConfiguration {
    @Bean
    fun sessionRegistry(): SessionRegistry? = SessionRegistryImpl()

    @Bean
    fun httpSessionEventPublisher(): HttpSessionEventPublisher? = HttpSessionEventPublisher()

    @Bean
    fun cookieSerializer(): CookieSerializer {
        val serializer = DefaultCookieSerializer()
        serializer.setCookieName("SESSION-ID")
        serializer.setCookieMaxAge(10)
        serializer.setCookiePath("/")
        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$")
        serializer.setUseSecureCookie(true)
        serializer.setSameSite("none")
        serializer.setUseHttpOnlyCookie(true)
        return serializer
    }
}
