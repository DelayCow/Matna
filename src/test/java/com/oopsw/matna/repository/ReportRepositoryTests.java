package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.GroupBuyReport;
import com.oopsw.matna.repository.entity.MemberReport;
import com.oopsw.matna.repository.entity.Report;
import com.oopsw.matna.vo.AllReportVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ReportRepositoryTests {
    @Autowired
    MemberReportRepository memberReportRepository;
    @Autowired
    GroupBuyReportRepository groupBuyReportRepository;

//    @Transactional
//    @Test
//    public void findAll(){
//        Integer reportNo = 1;  // 테스트할 report_no
//
//        Report report = reportRepository.findById(reportNo).get();
//
//        ReportVO vo = ReportVO.builder()
//                .reportNo(report.getReportNo())
//                .reporterNo(report.getReporter().getMemberNo())  // ManyToOne 접근
//                .status(report.getStatus())
//                .reportedDate(report.getReportedDate())
//                .imageUrl(report.getImageUrl())
//                .reason(report.getReason())
//                .build();
//
//        System.out.println(vo);
//    }
}
