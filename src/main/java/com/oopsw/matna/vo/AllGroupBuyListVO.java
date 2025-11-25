package com.oopsw.matna.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class AllGroupBuyListVO {
    private Integer groupBuyNo;
    private String status;
    private LocalDateTime inDate;
    private String nickname;
    private Integer creatorNo;
    private String title;
    private String groupBuyCase;
}
