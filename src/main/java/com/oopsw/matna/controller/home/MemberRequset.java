package com.oopsw.matna.controller.home;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MemberRequset {
    private String memberId;
    private String password;
    private String accountName;
    private String nickname;
    private String bank;
    private String accountNumber;
    private String address;
}
