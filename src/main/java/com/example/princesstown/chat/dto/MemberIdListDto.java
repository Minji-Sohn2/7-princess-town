package com.example.princesstown.chat.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MemberIdListDto {
    private List<ChatMemberIdDto> memberIdList;

    public MemberIdListDto(List<ChatMemberIdDto> memberIdList) {
        this.memberIdList = memberIdList;
    }
}
