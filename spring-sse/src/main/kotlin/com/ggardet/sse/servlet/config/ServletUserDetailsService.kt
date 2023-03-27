package com.ggardet.sse.servlet.config

import com.ggardet.sse.common.mapper.UserMapper
import com.ggardet.sse.common.model.CustomUser.Companion.ROLE_ADMIN
import com.ggardet.sse.common.model.CustomUser.Companion.ROLE_USER
import org.springframework.context.annotation.Profile
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Profile("servlet")
@Service
class ServletUserDetailsService : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        val user: UserDetails = User
            .withUsername("user")
            .password("{noop}user")
            .roles(ROLE_USER, ROLE_ADMIN)
            .build()
        return UserMapper.mapToCustomUser(user)
    }
}
