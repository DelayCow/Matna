package com.oopsw.matna.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class GroupBuyParticipantVO {
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
