package com.oopsw.matna.dao;

import com.oopsw.matna.vo.AllReportVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class ReportDAOTests {
    @Autowired
    ReportDAO reportDAO;

    @Test
    void getAllReports() {
        LocalDate startDate = LocalDate.of(2025, 11, 19);
        LocalDate endDate = LocalDate.of(2025, 11, 25);
        String reportStatus = "WIP";
        String reportCase = "group_buys";
        String keyword = "";
        List<AllReportVO> list = reportDAO.getReports(startDate, endDate, reportStatus, reportCase,keyword);
        System.out.println(list);
    }

}
