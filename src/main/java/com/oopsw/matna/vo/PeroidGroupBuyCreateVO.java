package com.oopsw.matna.vo;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@Data
@ToString
public class PeroidGroupBuyCreateVO {
    private Integer ingredientNo;
    private Integer creatorNo;
    private String title;
    private Integer buyEndDate;
    private Integer shareEndDate;
    private LocalTime shareTime;
    private String shareLocation;
    private String shareDetailAddress;
    private Integer price;
    private Integer quantity;
    private String unit;
    private Integer feeRate;
    private String imageUrl;
    private String content;
    private String itemSaleUrl;
    private LocalDateTime dueDate;
    private Integer maxParticipants;
}
