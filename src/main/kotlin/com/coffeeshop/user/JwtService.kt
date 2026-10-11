package com.coffeeshop.user

import com.coffeeshop.user.dto.UserResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class JwtService(
    private val jwtEncoder: JwtEncoder,
    @Value("\${spring.security.jwt.expiration-seconds:3600}")
    private val expirationSeconds: Long
) {
    fun generateToken(user: UserResponse): String {
        val now = Instant.now()

        val claims = JwtClaimsSet.builder()
            .issuer("coffee-shop")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(expirationSeconds))
            .subject(user.email)
            .claim("userId", user.id)
            .claim("roles", listOf(user.role))
            .build()

        val header = JwsHeader.with(MacAlgorithm.HS256).build()

        return jwtEncoder.encode(
            JwtEncoderParameters.from(header, claims)
        ).tokenValue
    }
}