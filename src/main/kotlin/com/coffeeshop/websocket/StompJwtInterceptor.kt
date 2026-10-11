package com.coffeeshop.websocket

import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.stereotype.Component

@Component
class StompJwtInterceptor(
    private val jwtDecoder: JwtDecoder
) : ChannelInterceptor {

    override fun preSend(
        message: Message<*>,
        channel: MessageChannel
    ): Message<*> {
        val accessor = MessageHeaderAccessor.getAccessor(
            message,
            StompHeaderAccessor::class.java
        ) ?: return message

        when (accessor.command) {
            StompCommand.CONNECT -> {
                val token = accessor.getFirstNativeHeader("Authorization")
                    ?.removePrefix("Bearer ")
                    ?: throw AccessDeniedException("Missing JWT")

                val jwt = try {
                    jwtDecoder.decode(token)
                } catch (ex: Exception) {
                    throw AccessDeniedException("Invalid JWT")
                }

                val roles = jwt.getClaimAsStringList("roles").orEmpty()
                val authorities = roles.map {
                    SimpleGrantedAuthority("ROLE_$it")
                }

                accessor.user = UsernamePasswordAuthenticationToken(
                    jwt.subject,
                    null,
                    authorities
                )
            }

            StompCommand.SUBSCRIBE -> {
                if (accessor.user == null) {
                    throw AccessDeniedException("Authentication required")
                }

                if (accessor.destination != "/user/queue/orders") {
                    throw AccessDeniedException("Subscription denied")
                }
            }

            StompCommand.SEND -> {
                throw AccessDeniedException("Client messages are not allowed")
            }

            else -> Unit
        }

        return message
    }
}