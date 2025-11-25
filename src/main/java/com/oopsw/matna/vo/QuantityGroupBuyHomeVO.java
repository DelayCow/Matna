package com.oopsw.matna.vo;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@ToString
public class QuantityGroupBuyHomeVO {
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

    private String shareLocation;
}
