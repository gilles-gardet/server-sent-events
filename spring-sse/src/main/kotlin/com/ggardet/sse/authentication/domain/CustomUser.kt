package com.ggardet.sse.authentication.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

data class CustomUser(
    @JsonIgnore private val username: String,
    @JsonIgnore private val password: String,
    private val enabled: Boolean,
    @JsonIgnore private val accountNonExpired: Boolean,
    @JsonIgnore private val credentialsNonExpired: Boolean,
    @JsonIgnore private val accountNonLocked: Boolean,
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
