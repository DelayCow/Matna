package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.GroupBuyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupBuyReportRepository extends JpaRepository<GroupBuyReport, Integer> {
    List<GroupBuyReport> findAllByOrderByGroupBuyReportNoDesc();
}
