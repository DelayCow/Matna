package com.oopsw.matna.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MemberProfileVO {
    private String nickname;
    private String imageUrl;
    private Integer point;
}
