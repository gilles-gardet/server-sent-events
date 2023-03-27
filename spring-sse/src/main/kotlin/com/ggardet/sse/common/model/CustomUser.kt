package com.ggardet.sse.common.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

class CustomUser(
    private val username: String,
    private val password: String,
    private val authorities: Collection<GrantedAuthority>,
    private val enabled: Boolean,
    val authDate: LocalDateTime,
    val email: String
) : UserDetails {
    companion object {
        const val ROLE_ADMIN = "ADMIN"
        const val ROLE_USER = "USER"
    }
    override fun getAuthorities(): Collection<GrantedAuthority> = authorities
    override fun getPassword(): String = password
    override fun getUsername(): String = username
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = enabled
}
