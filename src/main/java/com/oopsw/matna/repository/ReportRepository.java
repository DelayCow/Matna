package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    boolean findByReportNo(Integer reportNo);
}
