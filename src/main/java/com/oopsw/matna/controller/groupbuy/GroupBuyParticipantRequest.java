package com.oopsw.matna.controller.groupbuy;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Data
@ToString
public class GroupBuyParticipantRequest {
    private Integer groupParticipantNo;
    private Integer participantNo;
    private Integer groupBuyNo;
    private Integer myQuantity;
    private LocalDateTime participatedDate;
    private LocalDateTime receiveDate;
    private Integer initialPaymentPoint;
    private Integer finalPaymentPoint;
    private LocalDateTime cancelDate;
}
