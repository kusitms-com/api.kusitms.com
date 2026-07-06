package com.kusitms.website.global.config;

import com.kusitms.website.domain.chat.repository.ChatRoomRepository;
import com.kusitms.website.global.auth.UserPrincipal;
import com.kusitms.website.global.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor == null) {
                    return message;
                }

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authorization = accessor.getFirstNativeHeader("Authorization");
                    if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
                        throw new IllegalArgumentException("웹소켓 인증 토큰이 필요합니다.");
                    }

                    String token = authorization.substring(7);
                    if (!jwtTokenProvider.validateToken(token)) {
                        throw new IllegalArgumentException("유효하지 않은 웹소켓 토큰입니다.");
                    }

                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    accessor.setUser(authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    Authentication authentication = (Authentication) accessor.getUser();
                    validateSubscription(authentication, accessor.getDestination());
                }

                return message;
            }
        });
    }

    private void validateSubscription(Authentication authentication, String destination) {
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new IllegalArgumentException("웹소켓 인증 정보가 올바르지 않습니다.");
        }
        if (!StringUtils.hasText(destination)) {
            throw new IllegalArgumentException("구독 대상이 올바르지 않습니다.");
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getPk();

        if (destination.startsWith("/sub/chat/users/")) {
            String prefix = "/sub/chat/users/";
            String suffix = "/rooms";
            if (!destination.endsWith(suffix)) {
                throw new IllegalArgumentException("구독 대상이 올바르지 않습니다.");
            }

            String targetUserId = destination.substring(prefix.length(), destination.length() - suffix.length());
            if (!String.valueOf(userId).equals(targetUserId)) {
                throw new IllegalArgumentException("다른 사용자의 채팅 목록은 구독할 수 없습니다.");
            }
            return;
        }

        if (destination.startsWith("/sub/chat/rooms/")) {
            String roomIdValue = destination.substring("/sub/chat/rooms/".length());
            Long roomId;
            try {
                roomId = Long.valueOf(roomIdValue);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("구독 대상 채팅방이 올바르지 않습니다.");
            }

            if (!chatRoomRepository.existsParticipantByChatRoomId(roomId, userId)) {
                throw new IllegalArgumentException("해당 채팅방을 구독할 수 없습니다.");
            }
            return;
        }

        throw new IllegalArgumentException("허용되지 않은 구독 대상입니다.");
    }
}
