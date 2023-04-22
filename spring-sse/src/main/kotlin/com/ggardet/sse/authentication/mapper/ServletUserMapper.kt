package com.ggardet.sse.authentication.mapper

import com.ggardet.sse.authentication.domain.ServletUser
import org.springframework.security.core.userdetails.UserDetails

class ServletUserMapper {
    companion object {
        fun mapToServletUser(userDetails: UserDetails): ServletUser {
            val username = userDetails.username
            return ServletUser(
                username,
                userDetails.password,
                userDetails.isEnabled,
                userDetails.isAccountNonExpired,
                userDetails.isCredentialsNonExpired,
                userDetails.isAccountNonLocked,
                ArrayList(userDetails.authorities)
            )
        }
    }
}
