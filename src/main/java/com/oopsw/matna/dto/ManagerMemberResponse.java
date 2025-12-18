package com.oopsw.matna.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class ManagerMemberResponse {
    private Integer memberNo;
    private String memberId;
    private String nickname;
    private String imageUrl;
    private LocalDateTime banDate;
    private String accountStatus;
    private LocalDateTime inDate;
}
