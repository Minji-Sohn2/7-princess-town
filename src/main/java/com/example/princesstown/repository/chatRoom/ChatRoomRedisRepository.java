package com.example.princesstown.repository.chatRoom;

import com.example.princesstown.entity.ChatRoom;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
@Slf4j(topic = "RedisRepository")
public class ChatRoomRedisRepository {

    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장
    public static final String ENTER_INFO = "ENTER_INFO";

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, ChatRoom> hashOpsChatRoom;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;

    public List<ChatRoom> findAllRoom() {
        return hashOpsChatRoom.values(CHAT_ROOMS);
    }

    public ChatRoom findRoomById(String id) {
        return hashOpsChatRoom.get(CHAT_ROOMS, id);
    }

    /*
        채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장
     */
    public void saveChatRoomRedis(ChatRoom chatRoom) {
        log.info(chatRoom.getChatRoomName());
        hashOpsChatRoom.put(CHAT_ROOMS, chatRoom.getChatRoomId(), chatRoom);
    }

    /*
        채팅방 삭제
     */
    public void deleteChatRoomRedis(ChatRoom chatRoom) {
        hashOpsChatRoom.delete(CHAT_ROOMS, chatRoom.getChatRoomId());
    }

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
