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
    private LocalDateTime inDate;
    private LocalDateTime buyDate;
    private LocalDateTime arriveDate;
    private LocalDateTime receiveDate;
    private Integer remainingQuantity;
    private Integer myQuantity;
    private LocalDateTime participatedDate;
    private Integer finalPaymentPoint;
    private Integer participantExMe;
}
