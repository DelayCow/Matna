package com.oopsw.matna.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class GroupBuyListVO {
    private Integer groupBuyNo;
    private String title;
    private String status;
    private String imageUrl;
    private String unit;
    private LocalDateTime inDate;
    private LocalDateTime buyEndDate;
    private LocalDateTime shareEndDate;
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
