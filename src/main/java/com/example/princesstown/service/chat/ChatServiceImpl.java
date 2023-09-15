package com.example.princesstown.service.chat;

import com.example.princesstown.dto.chat.ChatMessageDto;
import com.example.princesstown.entity.User;
import com.example.princesstown.entity.chat.ChatMessage;
import com.example.princesstown.repository.chat.ChatMessageRepository;
import com.example.princesstown.repository.user.UserRepository;
import com.example.princesstown.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
@Slf4j(topic = "ChatService")
public class ChatServiceImpl implements ChatService {

    private final JwtUtil jwtUtil;
    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public String getRoomId(String destination) {
        log.info("getRoomId 원래 destination -> " + destination);
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }


    // 채팅방에 메시지 발송
    @Override
    public void sendChatMessage(ChatMessageDto chatMessageDto, String token) {
        log.info("sendMessage 메서드 시작");

        // 메세지 header 의 토큰으로 sender 설정
        String username = jwtUtil.getUsernameFromJwt(token);
        User user = findUserByUsername(username);

        chatMessageDto.setSender(user.getNickname());

        // 나가기 메세지일 경우
        if (ChatMessageDto.MessageType.QUIT.equals(chatMessageDto.getType())) {
            chatMessageDto.setMessage(chatMessageDto.getSender() + "님이 방에서 나갔습니다.");
            chatMessageDto.setSender("[알림]");
        }

        // 메세지 저장
        chatMessageRepository.save(new ChatMessage(user, chatMessageDto));

        // 메세지 dto에 현재 시각 설정 (사용자에게 바로 보내질 시각)
        chatMessageDto.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm")));
        log.info("메세지 발송 시각? " + chatMessageDto.getCreatedAt());

        // 발송
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessageDto);
    }

    @Override
    public void sendImageMessage(Long roomId, String imageUrl, User user) {
        log.info("sendImageMessage 메서드 시작");

        ChatMessageDto chatMessageDto = new ChatMessageDto(roomId, imageUrl);
        chatMessageDto.setSender(user.getNickname());

        // 메세지 저장
        chatMessageRepository.save(new ChatMessage(user, chatMessageDto));

        // 메세지 dto에 현재 시각 설정 (사용자에게 바로 보내질 시각)
        chatMessageDto.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm")));
        log.info("메세지 발송 시각? " + chatMessageDto.getCreatedAt());

        // 발송
//        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessageDto);
        log.info("imageUrl : " + imageUrl);
    }

    // 받아온 이미지(바이너리) 메세지 s3에 업로드
//    private void binaryImageUpload (ChatMessageDto chatMessageDto) {
//        String[] strings = chatMessageDto.getImgData().split(","); // ","을 기준으로 바이트 코드를 나눠준다
//        String base64Image = strings[1];
//        String extension = ""; // if 문을 통해 확장자명을 정해줌
//        if (strings[0].equals("data:image/jpeg;base64")) {
//            extension = "jpeg";
//        } else if (strings[0].equals("data:image/png;base64")){
//            extension = "png";
//        } else {
//            extension = "jpg";
//        }
//
//        byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image); // 바이트 코드를
//
//        File tempFile = File.createTempFile("image", "." + extension); // createTempFile을 통해 임시 파일을 생성해준다. (임시파일은 지워줘야함)
//        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
//            outputStream.write(imageBytes); // OutputStream outputStream = new FileOutputStream(tempFile)을 통해 생성한 outputStream 객체에 imageBytes를 작성해준다.
//        }
//    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new NullPointerException("존재하지 않는 사용자"));
    }
}
