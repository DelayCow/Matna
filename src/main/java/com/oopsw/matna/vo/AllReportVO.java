package com.oopsw.matna.vo;

import com.oopsw.matna.repository.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllReportVO {
    private Integer reportNo;
    private Integer reporterNo;
    private String reporterId;
    private String reporterName;
    private String status;
    private LocalDateTime reportedDate;
    private String imageUrl;
    private String reason;
    private Integer targetNo;
    private Integer groupBuyNo;
    private String reportCase;
}
