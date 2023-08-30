package com.example.princesstown.service.chat;

import com.example.princesstown.repository.chatRoom.ChatRoomRedisRepository;
import com.example.princesstown.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/*
    stomp 요청을 받을 때 거치는 클래스
 */
@Slf4j(topic = "StompHandler")
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final ChatService chatService;
    private final ChatRoomRedisRepository chatRoomRedisRepository;

    /*
        Websocket 을 통해 들어온 요청 처리하기 전
        jwt token 유효성 검증
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("preSend 함수 시작");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // websocket 연결 시
        if (StompCommand.CONNECT == accessor.getCommand()) {
            log.info("StompCommand.CONNECT");
            String rawToken = accessor.getFirstNativeHeader("Authorization");
            log.info("rawToken : " + rawToken);     // bearer 들어있음

            String tokenValue = jwtUtil.substringToken(rawToken);
            log.info("token : " + tokenValue);

            boolean result = jwtUtil.validateToken(tokenValue);
            log.info("validate 결과 : " + result);

        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            log.info("StompCommand.SUBSCRIBE");

            // header정보에서 구독 destination정보를 얻고, roomId를 추출한다.
            String roomId = chatService.getRoomId(Optional.ofNullable(
                            (String) message.getHeaders()
                                    .get("simpDestination"))
                    .orElse("InvalidRoomId")
            );

            log.info("roomId : " + roomId);

            // 채팅방에 들어온 클라이언트 sessionId를 roomId와 맵핑해 놓는다.
            // (나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함)
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            chatRoomRedisRepository.setUserEnterInfo(sessionId, roomId);

            log.info("SUBSCRIBE 끝");

        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료

            // 연결이 종료된 클라이언트 sesssionId로 채팅방 id를 얻는다.
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = chatRoomRedisRepository.getUserEnterRoomId(sessionId);

            // 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제한다.
            chatRoomRedisRepository.removeUserEnterInfo(sessionId);
            log.info("DISCONNECTED {}, {}", sessionId, roomId);
        }

        return message;
    }
}

