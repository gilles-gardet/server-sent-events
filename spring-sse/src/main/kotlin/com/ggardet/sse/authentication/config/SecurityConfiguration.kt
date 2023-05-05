package com.ggardet.sse.authentication.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggardet.sse.authentication.domain.CustomUser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.csrf.CsrfTokenRepository
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity(debug = false)
class SecurityConfiguration(val csrfTokenRepository: CsrfTokenRepository, val objectMapper: ObjectMapper) {
    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
//            .cors { it.configurationSource { CorsConfiguration().applyPermitDefaultValues() } }
//            .csrf { it.csrfTokenRepository(csrfTokenRepository) }
            .cors().disable()
            .csrf().disable()
            .authorizeHttpRequests {
                it.mvcMatchers(HttpMethod.GET, "/notifications", "/me")
                    .permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin {
                it.loginProcessingUrl("/authentication")
                    .successHandler { _, httpServletResponse, authentication ->
                        run {
                            httpServletResponse.status = HttpServletResponse.SC_OK
                            httpServletResponse.characterEncoding = Charsets.UTF_8.name()
                            httpServletResponse.contentType = MediaType.APPLICATION_JSON_VALUE
                            val user = authentication.principal as CustomUser
                            val json = objectMapper.writeValueAsString(user)
                            httpServletResponse.writer.println(json)
                        }
                    }
                    .failureHandler { _, httpServletResponse, _ ->
                        run {
                            httpServletResponse.status = HttpServletResponse.SC_UNAUTHORIZED
                            httpServletResponse.characterEncoding = Charsets.UTF_8.name()
                            httpServletResponse.writer.write("Invalid credentials")
                        }
                    }
                    .permitAll()
            }
            .logout {
                it.logoutRequestMatcher(AntPathRequestMatcher("/logout", HttpMethod.GET.name))
                    .logoutSuccessHandler { _, httpServletResponse, _ ->
                        run {
                            httpServletResponse.status = HttpServletResponse.SC_OK
                            httpServletResponse.characterEncoding = Charsets.UTF_8.name()
                            httpServletResponse.writer.write("Logout success")
                        }
                    }
                    .invalidateHttpSession(true)
                    .deleteCookies("SESSION-ID")
                    .permitAll()
            }
            .exceptionHandling {
                it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                    .accessDeniedHandler { _, httpServletResponse, _ ->
                        run {
                            httpServletResponse.status = HttpServletResponse.SC_UNAUTHORIZED
                            httpServletResponse.characterEncoding = Charsets.UTF_8.name()
                            httpServletResponse.writer.append("Access Denied")
                        }
                    }
            }
        return httpSecurity.build()
    }
}
