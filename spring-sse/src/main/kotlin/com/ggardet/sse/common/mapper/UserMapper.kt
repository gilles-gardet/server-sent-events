package com.ggardet.sse.common.mapper

import com.ggardet.sse.common.model.CustomUser
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

class UserMapper {
    companion object {
        fun mapToCustomUser(userDetails: UserDetails): CustomUser {
            val username = userDetails.username
            return CustomUser(
                username,
                userDetails.password,
                userDetails.authorities.map { SimpleGrantedAuthority(it.authority) }.toSet(),
                userDetails.isEnabled,
                LocalDateTime.now(),
                "$username@email.com"
            )
        }
    }
}
