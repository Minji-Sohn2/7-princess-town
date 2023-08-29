package com.example.princesstown.service.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendUsernames(String email, List<String> username) {
        SimpleMailMessage message = new  SimpleMailMessage();
        message.setTo(email);

        log.info("Sending email to: {}", email); // 로그 추가

        message.setSubject("아이디 찾기");

        StringBuffer sb = new StringBuffer();
        sb.append("가입하신 아이디는");
        sb.append(System.lineSeparator());

        for(int i=0;i<username.size()-1;i++) {
            sb.append(username.get(i));
            sb.append(System.lineSeparator());
        }
        sb.append(username.get(username.size()-1)).append("입니다");

        message.setText(sb.toString());

        new Thread(() -> {
            log.info("Sending email...");
            try {
                mailSender.send(message);
                log.info("Email sent successfully."); // 로그 추가
            } catch (Exception e) {
                log.error("Failed to send email: {}", e.getMessage(), e); // 에러 로그 추가
            }
        }).start();
    }

    public void sendTemporaryPassword(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("비밀번호 찾기 임시 비밀번호");

        // 난수 생성
        String tempPassword = UUID.randomUUID().toString().substring(0, 11);

        String text = "임시 비밀번호는 " + tempPassword + "입니다";

        message.setText(text);
        new Thread(() -> {
            log.info("Sending email...");
            try {
                mailSender.send(message);
                log.info("Email sent successfully.");
            } catch (Exception e) {
                log.error("Failed to send email: {}", e.getMessage(), e);
            }
        }).start();
    }
}