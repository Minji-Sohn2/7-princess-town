package com.example.princesstown.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfig {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    // Redis 저장소와 연결
    @Bean
    public RedisConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }


//    // 단일 Topic 사용을 위한 Bean 설정
//
//    @Bean
//    public ChannelTopic channelTopic() {
//        return new ChannelTopic("chatroom");
//    }
//
//
//    // redis에 발행(publish)된 메시지 처리를 위한 리스너 설정
//
//    @Bean
//    public RedisMessageListenerContainer redisMessageListener(MessageListenerAdapter listenerAdapter,
//                                                              ChannelTopic channelTopic) {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory());
//        container.addMessageListener(listenerAdapter, channelTopic);
//        return container;
//    }
//
//
//    // 실제 메시지를 처리하는 subscriber 설정 추가
//    @Bean
//    public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
//        return new MessageListenerAdapter(subscriber, "sendMessage");
//    }
//
//    // 어플리케이션에서 사용할 redisTemplate 설정
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(connectionFactory());
//
//        // RedisTemplate을 사용할 때 Spring-Redis 간 데이터 직렬화/역직렬화 시 사용하는 방식이 jdk 직렬화 방식
//        // 동작에는 문제가 없지만 redis-cli를 통해 데이터를 확인할 때 알아볼 수 없는 형태로 출력되기 때문
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
//        return redisTemplate;
//    }
}
