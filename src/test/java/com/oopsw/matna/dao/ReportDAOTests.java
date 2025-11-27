package com.oopsw.matna.dao;

import com.oopsw.matna.vo.AllReportVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class ReportDAOTests {
    @Autowired
    ReportDAO reportDAO;

    @Test
    void getAllReports() {
        LocalDateTime startDate = LocalDateTime.of(2025, 11, 19, 1, 1, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 11, 25, 1, 1, 1);
        String reportStatus = "WIP";
        String reportCase = "group_buys";
        String memberId = "";
        String nickname = "베베";
        List<AllReportVO> list = reportDAO.getReports(startDate, endDate, reportStatus, reportCase, memberId, nickname);
        System.out.println(list);
    }

}
