package com.oopsw.matna.vo;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@Data
@ToString
public class PeriodGroupBuyDetailVO {
    private Integer groupBuyNo;
    private String title;
    private String content;
    private String imageUrl;
    private Integer ingredientNo;
    private Integer maxParticipants;
    private Integer feeRate;
    private Integer price;
    private Integer quantity;
    private String unit;
    private String itemSaleUrl;
    private Integer periodGroupBuyNo;

    private Integer participants;

    private LocalDateTime inDate;
    private String shareLocation;
    private String shareDetailAddress;

    private Integer buyEndDate;
    private Integer shareEndDate;
    private LocalTime shareTime;

    private LocalDateTime dueDate;
    private Integer remainingTime;
    private String status;

    private String profileImageUrl;
    private String nickname;
}
