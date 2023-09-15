package com.example.princesstown.dto.chatRoom;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MemberInfoListDto {
    private List<ChatMemberInfoDto> chatMemberInfoList;

    public MemberInfoListDto(List<ChatMemberInfoDto> chatMemberInfoList) {
        this.chatMemberInfoList = chatMemberInfoList;
    }
}
