package com.oopsw.matna.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class ManagerGroupBuyResponse {
    private Integer groupBuyNo;
    private String status;
    private LocalDateTime inDate;
    private String creatorName;
    private String title;
}
