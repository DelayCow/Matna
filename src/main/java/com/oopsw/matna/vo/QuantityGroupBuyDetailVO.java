package com.oopsw.matna.vo;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QuantityGroupBuyDetailVO {
    // 기본 정보
    private Integer groupBuyNo;
    private Integer creatorNo;
    private String title;
    private String content;
    private String imageUrl;
    private Integer ingredientNo;
    private Integer feeRate;
    private String itemSaleUrl;

    // 수량 공구 정보
    private Integer quantityGroupBuyNo;
    private Integer shareAmount;
    private String unit;

    // 날짜/장소 정보
    private LocalDateTime inDate;
    private String shareLocation;
    private String shareDetailAddress;
    private Integer buyEndDate;
    private Integer shareEndDate;
    private String status;

    // 가격/수량 정보
    private Long pricePerUnit;
    private Integer remainingQty;

    // 생성자 정보
    private String creatorProfileUrl;
    private String creatorNickname;

    private Integer myQuantity;
    private Integer groupParticipantNo;
}