package com.example.princesstown.dto.chatRoom;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MemberIdListDto {
    private List<ChatMemberIdDto> memberIdList;

    public MemberIdListDto(List<ChatMemberIdDto> memberIdList) {
        this.memberIdList = memberIdList;
    }
}
