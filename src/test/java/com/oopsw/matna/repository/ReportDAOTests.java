package com.oopsw.matna.repository;

import com.oopsw.matna.vo.AllReportVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ReportDAOTests {
    @Autowired
    ReportDAO reportDAO;

    @Test
    void getAllReports() {
        List<AllReportVO> list = reportDAO.getReports();
        System.out.println(list);
    }
}
