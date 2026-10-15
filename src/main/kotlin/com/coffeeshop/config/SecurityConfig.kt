package com.coffeeshop.config

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import java.util.Base64
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        BCryptPasswordEncoder()

    @Bean
    fun jwtSecretKey(
        @Value("\${jwt.secret}") secret: String
    ): SecretKey {
        val keyBytes = Base64.getDecoder().decode(secret)

        require(keyBytes.size >= 32) {
            "JWT_SECRET должен содержать не менее 32 байт"
        }

        return SecretKeySpec(keyBytes, "HmacSHA256")
    }

    @Bean
    fun jwtEncoder(secretKey: SecretKey): JwtEncoder =
        NimbusJwtEncoder(ImmutableSecret<SecurityContext>(secretKey))

    @Bean
    fun jwtDecoder(secretKey: SecretKey): JwtDecoder =
        NimbusJwtDecoder
            .withSecretKey(secretKey)
            .macAlgorithm(MacAlgorithm.HS256)
            .build()

    @Bean
    fun jwtAuthenticationConverter(): Converter<Jwt, out AbstractAuthenticationToken> =
        Converter { jwt ->
            val roles = jwt.getClaimAsStringList("roles") ?: emptyList()

            val authorities = roles.map { role ->
                SimpleGrantedAuthority("ROLE_$role")
            }

            JwtAuthenticationToken(jwt, authorities, jwt.subject)
        }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationConverter: Converter<Jwt, out AbstractAuthenticationToken>,
        corsConfigurationSource: CorsConfigurationSource
    ): SecurityFilterChain {
        http
            .cors { cors -> cors.configurationSource(corsConfigurationSource) }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/auth/register",
                    "/api/auth/login",
                    "/ws",
                    "/ws/**"
                ).permitAll()

                it.requestMatchers(
                    HttpMethod.GET,
                    "/api/categories",
                    "/api/categories/**",
                    "/api/products",
                    "/api/products/**"
                ).permitAll()

                it.requestMatchers(
                    HttpMethod.PATCH,
                    "/api/orders/*/cancel"
                ).permitAll()

                it.requestMatchers(
                    HttpMethod.GET,
                    "/api/orders/admin"
                ).hasRole("ADMIN")

                it.requestMatchers(
                    HttpMethod.PATCH,
                    "/api/categories/**",
                    "/api/products/**",
                    "/api/orders/*/status"
                ).hasRole("ADMIN")

                it.requestMatchers(
                    HttpMethod.POST,
                    "/api/categories",
                    "/api/products"
                ).hasRole("ADMIN")

                it.requestMatchers(
                    HttpMethod.PUT,
                    "/api/categories/**",
                    "/api/products/**"
                ).hasRole("ADMIN")

                it.requestMatchers(
                    HttpMethod.DELETE,
                    "/api/categories/**",
                    "/api/products/**"
                ).hasRole("ADMIN")

                it.anyRequest().authenticated()
            }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .oauth2ResourceServer {
                it.jwt { jwt ->
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
                }
            }

        return http.build()
    }
}