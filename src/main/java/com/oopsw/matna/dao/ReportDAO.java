package com.oopsw.matna.dao;

import com.oopsw.matna.vo.AllReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReportDAO {
    List<AllReportVO> getReports(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status,
            @Param("reportCase") String reportCase,
            @Param("keyword") String keyword
    );
}
