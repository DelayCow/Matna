package com.oopsw.matna.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyListVO {
    private Integer periodGroupBuyNo;
    private Integer quantityGroupBuyNo;
    private Integer groupBuyNo;
    private String title;
    private String status;
    private String imageUrl; // 아마 썸네일
    private String unit;
    private LocalDateTime inDate;
    private Integer buyEndDate;
    private Integer shareEndDate;
    private LocalDateTime buyDate; // 구매 날짜
    private LocalDateTime arrivalDate; // 도착 날짜
    private String arrivalImageUrl; // 택배 이미지
    private LocalDateTime receiveDate; // 나눔 날짜
    private String receiptImageUrl; // 영수증 이미지
    private Integer remainingQuantity;
    private Integer myQuantity;
    private LocalDateTime participatedDate;
    private Integer finalPaymentPoint;
    private Integer participantExMe;
    private Integer totalSettlementPoint;
    private LocalDateTime DueDate; // 기간공구용 플래그
    private Integer groupParticipantNo;
    private String paymentNote;

}
