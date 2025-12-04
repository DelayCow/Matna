package com.oopsw.matna.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class QuantityListResponse {
    private Integer groupBuyNo;
    private String title;
    private String nickname;
    private String creatorImageUrl;
    private String groupBuyImageUrl;
    private LocalDateTime inDate;
    private String ingredientName;

    private Integer quantity;
    private Integer myQuantity;
    private Integer remainingQty;
    private String unit;
    private Integer pricePerUnit;
    private Integer shareAmount;
    private Double remainingRatio;

    private String shareLocation;
    private Integer quantityGroupBuyNo;
}
