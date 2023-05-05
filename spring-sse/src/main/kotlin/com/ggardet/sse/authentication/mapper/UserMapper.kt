package com.ggardet.sse.authentication.mapper

import com.ggardet.sse.authentication.domain.CustomUser
import org.springframework.security.core.userdetails.UserDetails

class UserMapper {
    companion object {
        fun mapToServletUser(userDetails: UserDetails): CustomUser {
            val username = userDetails.username
            return CustomUser(
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
