package com.example.princesstown.service.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendUsernames(String email, String username) {
        SimpleMailMessage message = new  SimpleMailMessage();
        message.setTo(email);
        message.setSubject("아이디 찾기");

        String text = "가입하신 아이디는 " + username + "입니다";

        message.setText(text);
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

    public void sendTemporaryPassword(String email, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("비밀번호 찾기 임시 비밀번호");

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

    public void sendDeactivateVerifyCode(String email, String deteactiveCode) {
        SimpleMailMessage message = new  SimpleMailMessage();
        message.setTo(email);
        message.setSubject("회원탈퇴");

        String text = "회원탈퇴를 위한 인증코드는 " + deteactiveCode + "입니다";

        message.setText(text);
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
}