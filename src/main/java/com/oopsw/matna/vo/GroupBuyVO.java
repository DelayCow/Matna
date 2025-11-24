package com.oopsw.matna.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@Data
public class GroupBuyVO {
    private Integer groupBuyNo;
    private Integer ingredientNo;
    private Integer creatorNo;
    private String title;
    private Integer buyEndDate;
    private Integer shareEndDate;
    private String shareLocation;
    private LocalTime shareTime;
    private String shareDetailAddress;
    private Integer price;
    private Integer quantity;
    private String unit;
    private Integer feeRate;
    private String imageUrl;
    private String content;
    private LocalDateTime inDate;
    private String itemSaleUrl;
    private Integer scrapCount;
    private String status;
    private String receiptImageUrl;
    private LocalDateTime buyDate;
    private String paymentNote;
    private String arrivalImageUrl;
    private LocalDateTime arrivalDate;
    private String cancelReason;
}
