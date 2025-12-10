package com.oopsw.matna.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class AllMemberListVO {
    private Integer memberNo;
    private String memberId;
    private String nickname;
    private String imageUrl;
    private LocalDateTime inDate;
    private LocalDateTime banDate;
    private String accountStatus;
}
