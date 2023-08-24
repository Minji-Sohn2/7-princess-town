package com.example.princesstown.service.message;

import com.example.princesstown.dto.response.ApiResponseDto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Getter
@Setter
@Service
public class MessageService {

    @Value("${nurigo.app.key}")
    private String appKey;

    @Value("${nurigo.app.secret}")
    private String appSecret;

    @Value("${nurigo.api.url}")
    private String apiUrl;

    private String numStr;

    public ResponseEntity<ApiResponseDto> sendVerificationCode(String phoneNumber) {
        DefaultMessageService messageService =  NurigoApp.INSTANCE.initialize(appKey, appSecret, apiUrl);

        Random ran = new Random();
        StringBuilder numStr = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            String random = Integer.toString(ran.nextInt(10));
            numStr.append(random);
        }

        setNumStr(numStr.toString());

        Message message = new Message();
        message.setFrom("01046358930");
        message.setTo(phoneNumber);
        message.setText(" 인증번호는 [" + numStr + "] 입니다.");

        try {
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException exception) {
            log.error("발송에 실패한 메세지 목록" + exception.getFailedMessageList());
            log.error("발송한 메세지" + exception.getMessage());
        } catch (Exception exception) {
            log.error("발송한 메세지" + exception.getMessage());
        }

        return ResponseEntity.status(200).body(new ApiResponseDto(HttpStatus.OK.value(), "메세지 전송 성공", numStr));
    }
}
