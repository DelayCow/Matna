package com.oopsw.matna.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class PeriodGroupBuyDetailVO {
    private Integer groupBuyNo;
    private String title;
    private String content;
    private String imageUrl;
    private Integer ingredientNo;
    private int maxParticipants;
    private double feeRate;
    private String itemSaleUrl;
    private Integer periodGroupBuyNo;

    private int participants;

    private LocalDateTime inDate;
    private String shareLocation;
    private String shareDetailAddress;

    private Integer purchasePeriodDays;
    private Integer shareEndDate;
    private LocalTime shareTime;

    private LocalDateTime dueDate;
    private Integer remainingTime;

    private String status;
    private String profileImageUrl;
    private String nickname;
}
