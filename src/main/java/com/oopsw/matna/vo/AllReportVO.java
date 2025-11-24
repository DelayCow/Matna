package com.oopsw.matna.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
public class AllReportVO {
    private Integer reportNo;
    private String memberId;
    private String nickname;
    private String status;
    private LocalDateTime reportedDate;
//    private String imageUrl;
    private String reason;
//    private Integer targetMemberNo;
//    private Integer groupBuyNo;
    private String reportCase;
}