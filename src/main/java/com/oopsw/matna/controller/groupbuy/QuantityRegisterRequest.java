package com.oopsw.matna.controller.groupbuy;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalTime;

@Builder
@Data
@ToString
public class QuantityRegisterRequest {
    private Integer ingredientNo;
    private String ingredientName;
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
    private Integer myQuantity;
    private Integer shareAmount;
    private Integer pricePerUnit;
}
