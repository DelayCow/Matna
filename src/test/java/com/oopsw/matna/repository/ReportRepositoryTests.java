package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Report;
import com.oopsw.matna.repository.*;
import com.oopsw.matna.repository.entity.*;
import com.oopsw.matna.vo.AllReportVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Transactional
@SpringBootTest
public class ReportRepositoryTests {
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private MemberReportRepository memberReportRepository;

    @Autowired
    private GroupBuyReportRepository groupBuyReportRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GroupBuyRepository groupBuyRepository;

    @Test
    public void findByReportNo() {
        System.out.println(reportRepository.findByReportNo(1));
    }

    @Test
    @Transactional
    @Commit
    public void addMemberReportTest() {
        AllReportVO report = AllReportVO.builder()
                .reporterNo(5)
                .imageUrl("image.url")
                .reason("신고해보기크크")
                .targetMemberNo(6).build();
        Member reporter = memberRepository.findById(report.getReporterNo()).get();
        Member target = memberRepository.findById(report.getTargetMemberNo()).get();
        Report savedReport = reportRepository.save(Report.builder()
                .reporter(reporter)
                .status("WIP")
                .imageUrl(report.getImageUrl())
                .reason(report.getReason())
                .reportedDate(LocalDateTime.now())
                .build());
        memberReportRepository.save(MemberReport.builder().report(savedReport).targetMember(target).build());
    }

    @Test
    public void ReportStatus() {
        Report r = reportRepository.findById(6)  // findById는 Optional 반환
                .orElseThrow(() -> new RuntimeException("Report가 없습니다."));
        r.setStatus("complete");
    }

    @Test
    @Transactional
    @Commit
    public void addGroupBuyReportTest() {
        AllReportVO report = AllReportVO.builder()
                .reporterNo(5)
                .imageUrl("image.url")
                .reason("신고해보기크크")
                .groupBuyNo(28).build();
        Member reporter = memberRepository.findById(report.getReporterNo()).get();
        GroupBuy target = groupBuyRepository.findById(report.getGroupBuyNo()).get();
        Report savedReport = reportRepository.save(Report.builder()
                .reporter(reporter)
                .status("WIP")
                .imageUrl(report.getImageUrl())
                .reason(report.getReason())
                .reportedDate(LocalDateTime.now())
                .build());
        groupBuyReportRepository.save(GroupBuyReport.builder().report(savedReport).groupBuy(target).build());
    }
}
