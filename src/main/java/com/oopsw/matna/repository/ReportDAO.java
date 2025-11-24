package com.oopsw.matna.repository;

import com.oopsw.matna.vo.AllReportVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportDAO {
    List<AllReportVO> getReports();
}
