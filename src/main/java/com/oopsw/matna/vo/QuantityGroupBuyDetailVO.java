package com.oopsw.matna.vo;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@ToString
public class QuantityGroupBuyDetailVO {
    private Integer groupBuyNo;
    private String title;
    private String content;
    private String imageUrl;
    private Integer ingredientNo;
    private Integer feeRate;
    private String itemSaleUrl;
    private Integer quantityGroupBuyNo;
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
