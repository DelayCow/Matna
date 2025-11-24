package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Report;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@SpringBootTest
public class ReportRepositoryTests {
    @Autowired
    ReportRepository reportRepository;

    @Test
    public void findByReportNo() {
        System.out.println(reportRepository.findByReportNo(1));
    }

    @Commit
    @Test
    public void ReportStatus() {
        Report r = reportRepository.findById(6)  // findById는 Optional 반환
                .orElseThrow(() -> new RuntimeException("Report가 없습니다."));
        r.setStatus("complete");
    }
}
