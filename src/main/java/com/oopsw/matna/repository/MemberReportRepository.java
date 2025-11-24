package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.MemberReport;
import com.oopsw.matna.vo.AllReportVO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberReportRepository extends JpaRepository<MemberReport, Integer> {
    List<MemberReport> findByTargetMemberNoIsNotNull();
    List<MemberReport> findAllByOrderByMemberReportNoDesc();
}
