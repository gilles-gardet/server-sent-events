package com.ggardet.sse.authentication.service

import com.ggardet.sse.authentication.mapper.UserMapper
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsService : UserDetailsService {
    companion object {
        const val ROLE_ADMIN = "ADMIN"
        const val ROLE_USER = "USER"
    }
    override fun loadUserByUsername(username: String?): UserDetails {
        val user: UserDetails = User
            .withUsername("user")
            .password("{noop}user")
            .roles(ROLE_USER, ROLE_ADMIN)
            .build()
        val servletCustomUser = com.ggardet.sse.authentication.domain.CustomUser(
            user.username,
            user.password,
            user.isEnabled,
            user.isAccountNonExpired,
            user.isCredentialsNonExpired,
            user.isAccountNonLocked,
            ArrayList(user.authorities)
        )
        return UserMapper.mapToServletUser(servletCustomUser)
    }
}
