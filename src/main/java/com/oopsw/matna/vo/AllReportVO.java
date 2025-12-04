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
public class AllReportVO {
    private Integer reportNo;
    private Integer reporterNo;
    private String status;
    private LocalDateTime reportedDate;
    private String imageUrl;
    private String reason;
    private Integer targetMemberNo;
    private Integer groupBuyNo;
}
