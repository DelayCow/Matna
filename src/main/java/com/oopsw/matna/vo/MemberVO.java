package com.oopsw.matna.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Builder
@Data
public class MemberVO {
    private Integer memberNo;
    private String memberId;
    private String password;
    private String accountName;
    private String nickname;
    private String bank;
    private String accountNumber;
    private LocalDateTime inDate;
    private LocalDateTime delDate;
    private String role;
    private LocalDateTime banDate;
    private Integer point;
    private String imageUrl;
    private String address;
}
