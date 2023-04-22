package com.ggardet.sse.authentication.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

data class ServletUser(
    private val username: String,
    private val password: String,
    private val enabled: Boolean,
    private val accountNonExpired: Boolean,
    private val credentialsNonExpired: Boolean,
    private val accountNonLocked: Boolean,
    private val authorities: MutableCollection<GrantedAuthority>
) : UserDetails {
    val authDate: LocalDateTime = LocalDateTime.now()
    override fun getAuthorities(): MutableCollection<GrantedAuthority> = authorities
    override fun getPassword(): String = password
    override fun getUsername(): String = username
    override fun isAccountNonExpired(): Boolean = accountNonExpired
    override fun isAccountNonLocked(): Boolean = accountNonLocked
    override fun isCredentialsNonExpired(): Boolean = credentialsNonExpired
    override fun isEnabled(): Boolean = enabled
}
