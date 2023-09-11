package com.example.princesstown;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.TimeZone;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
public class PrincesstownApplication {

    static {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");  // 추가
    }

    public static void main(String[] args) {
        SpringApplication.run(PrincesstownApplication.class, args);
    }
}
