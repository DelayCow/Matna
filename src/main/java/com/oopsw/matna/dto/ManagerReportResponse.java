package com.oopsw.matna.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class ManagerReportResponse {
    private Integer managerReportId;
    private String status;
    private LocalDateTime reportedDate;
    private String reporterName;
    private String reporterImageUrl;
    private String imageUrl;
    private String reason;
    private Integer targetMemberNo;
    private String targetName;
    private String targetImageUrl;
    private Integer groupBuyNo;
    private String groupBuyTitle;
    private String groupBuyImageUrl;
    private String type;
}
