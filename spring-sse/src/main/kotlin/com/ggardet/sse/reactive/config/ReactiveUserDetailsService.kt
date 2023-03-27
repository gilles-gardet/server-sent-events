package com.ggardet.sse.reactive.config

import com.ggardet.sse.common.mapper.UserMapper
import com.ggardet.sse.common.model.CustomUser.Companion.ROLE_ADMIN
import com.ggardet.sse.common.model.CustomUser.Companion.ROLE_USER
import org.springframework.context.annotation.Profile
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Profile("reactive")
@Service
class ReactiveUserDetailsService : ReactiveUserDetailsService {
    override fun findByUsername(username: String): Mono<UserDetails> {
        val user = User.withUsername("user")
            .password("{noop}user")
            .roles(ROLE_USER, ROLE_ADMIN)
            .build()
        return UserMapper.mapToCustomUser(user).let { Mono.just(it) }
    }
}
