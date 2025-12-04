package com.oopsw.matna.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
public class MemberProfileListResponse {

    private int memberNo;
    private String nickname;
    private int points;
    private String imageUrl;
}
