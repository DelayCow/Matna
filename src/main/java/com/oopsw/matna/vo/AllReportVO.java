package com.oopsw.matna.vo;

import com.oopsw.matna.repository.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllReportVO {
    private Integer reportNo;
    private Integer reporterNo;
    private String reporterId;
    private String reporterName;
    private String reporterImageUrl;
    private String status;
    private LocalDateTime reportedDate;
    private String imageUrl;
    private String reason;
    private Integer targetMemberNo;
    private Integer targetNo;
    private String targetName;
    private String targetImageUrl;
    private Integer groupBuyNo;
    private String groupBuyTitle;
    private String groupBuyImageUrl;
    private String reportCase;
}
