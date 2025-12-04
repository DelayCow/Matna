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
    private Integer groupBuyNo;
    private String title;
    private String status;
    private String imageUrl;
    private String unit;
    private LocalDateTime inDate;
    private Integer buyEndDate;
    private Integer shareEndDate;
    private LocalDateTime buyDate;
    private LocalDateTime arrivalDate;
    private LocalDateTime receiveDate;
    private Integer remainingQuantity;
    private Integer myQuantity;
    private LocalDateTime participatedDate;
    private Integer finalPaymentPoint;
    private Integer participantExMe;
    private Integer totalSettlementPoint;
}
