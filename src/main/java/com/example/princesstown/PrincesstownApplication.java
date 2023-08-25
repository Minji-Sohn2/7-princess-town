package com.example.princesstown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PrincesstownApplication {

    static {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");  // 추가
    }

    public static void main(String[] args) {
        SpringApplication.run(PrincesstownApplication.class, args);
    }

}
