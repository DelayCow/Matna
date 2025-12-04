package com.oopsw.matna.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@Data
@ToString
public class PeriodDetailInfo {
    private Integer groupBuyNo;
    private String title;
    private String content;
    private String imageUrl;
    private Integer ingredientNo;
    private int maxParticipants;
    private Integer feeRate;
    private Integer price;
    private Integer quantity;
    private String unit;
    private String itemSaleUrl;
    private Integer periodGroupBuyNo;

    private int participants;

    private LocalDateTime inDate;
    private String shareLocation;
    private String shareDetailAddress;

    private Integer buyEndDate;
    private Integer shareEndDate;
    private LocalTime shareTime;

    private LocalDateTime dueDate;
    private Integer remainingTime;
    private String status;

}
