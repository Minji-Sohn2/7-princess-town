package com.example.princesstown.service.message;

import com.example.princesstown.dto.response.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

//    @Value("${nurigo.app.key}")
//    private String appKey;
//
//    @Value("${nurigo.app.secret}")
//    private String appSecret;
//
//    @Value("${nurigo.api.url}")
//    private String apiUrl;
//
//    @Value("${phoneNumber}")
//    private String sendPhoneNumber;

    private final StringRedisTemplate redisTemplate;


    public ResponseEntity<ApiResponseDto> sendVerificationCode(String phoneNumber) {
//        DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(appKey, appSecret, apiUrl);

        if (phoneNumber != null) {
            Random ran = new Random();
            StringBuilder numStr = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                String random = Integer.toString(ran.nextInt(10));
                numStr.append(random);
            }

            // Redis를 사용하여 "key" : phoneNumber, "value" : numStr.toString(), "양" : 5, "단위" : minute로 설정
            redisTemplate.opsForValue().set(phoneNumber, numStr.toString(), 5, TimeUnit.MINUTES); // 5분 후 만료

            return ResponseEntity.status(200).body(new ApiResponseDto(HttpStatus.OK.value(), "메세지 전송 성공", null));
        }
//
//        Message message = new Message();
//        message.setFrom(sendPhoneNumber);
//        message.setTo(phoneNumber);
//        message.setText(" 인증번호는 [" + numStr + "] 입니다.");
//
//        try {
//            messageService.send(message);
//        } catch (NurigoMessageNotReceivedException exception) {
//            log.error("발송에 실패한 메세지 목록" + exception.getFailedMessageList());
//            log.error("발송한 메세지" + exception.getMessage());
//        } catch (Exception exception) {
//            log.error("발송한 메세지" + exception.getMessage());
//        }

        return ResponseEntity.status(200).body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "메세지 전송 실패", null));
    }

    public ResponseEntity<ApiResponseDto> verifyCode(String phoneNumber, String inputCode) {
        String storedCode = redisTemplate.opsForValue().get(phoneNumber);

        if (storedCode == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "인증 번호가 만료되었거나 없습니다.", null));
        }

        if (inputCode.equals(storedCode)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto(HttpStatus.OK.value(), "인증 성공", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "인증 실패", null));
        }
    }
}