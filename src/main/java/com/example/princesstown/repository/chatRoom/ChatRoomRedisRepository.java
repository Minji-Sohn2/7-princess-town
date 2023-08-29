package com.example.princesstown.repository.chatRoom;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
@Slf4j(topic = "RedisRepository")
public class ChatRoomRedisRepository {

    // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장
    public static final String ENTER_INFO = "ENTER_INFO";

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;

    /*
        유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
     */
    public void setUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }

    /*
        유저 세션으로 입장해 있는 채팅방 ID 조회
     */
    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    /*
        유저 세션정보와 맵핑된 채팅방ID 삭제
     */
    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }
}
