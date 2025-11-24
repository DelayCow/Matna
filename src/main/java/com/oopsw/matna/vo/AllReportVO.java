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
    private LocalDateTime reportedDate;
    private String status;
    private String reason;
    private String reportCase;
//    private String imageUrl;
//    private Integer targetMemberNo;
//    private Integer groupBuyNo;

}