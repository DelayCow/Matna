package com.oopsw.matna.dao;

import com.oopsw.matna.vo.AllReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReportDAO {
    List<AllReportVO> getReports(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("reportStatus") String status,
            @Param("reportCase") String reportCase,
            @Param("memberId") String memberId,
            @Param("nickname") String nickname
    );
}
