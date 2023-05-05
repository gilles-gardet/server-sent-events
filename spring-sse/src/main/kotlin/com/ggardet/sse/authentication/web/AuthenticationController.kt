package com.ggardet.sse.authentication.web

import com.ggardet.sse.authentication.service.UserDetailsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class AuthenticationController(val userDetailsService: UserDetailsService) {
    @PostMapping("/authentication?{username}&{password}")
    fun authenticate(@PathVariable username: String, @PathVariable password: String): Boolean {
        return userDetailsService.loadUserByUsername(username).password == password
    }

    @GetMapping("/me")
    fun me(principal: Principal?): ResponseEntity<Any> =
        principal?.let { ResponseEntity.ok(it) } ?: ResponseEntity.accepted().build()
}
