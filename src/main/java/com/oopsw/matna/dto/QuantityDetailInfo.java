package com.oopsw.matna.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Data
@ToString
public class QuantityDetailInfo {
    private Integer groupBuyNo;
    private String title;
    private String content;
    private String imageUrl;
    private Integer ingredientNo;
    private Integer feeRate;
    private String itemSaleUrl;
    private Integer quantityGroupBuyNo;
    private Integer shareAmount;
    private String unit;
    private LocalDateTime inDate;
    private String shareLocation;
    private String shareDetailAddress;
    private Integer buyEndDate;
    private Integer shareEndDate;
    private String status;
    private Long pricePerUnit;
    private Integer remainingQty;
    private String creatorProfileUrl;
    private String creatorNickname;

    private String participantProfileUrl;
    private String participantNickname;
    private LocalDateTime participatedDate;
    private Integer myQuantity;

    private String recipeImageUrl;
    private String recipeTitle;
    private String authorNickname;
}
