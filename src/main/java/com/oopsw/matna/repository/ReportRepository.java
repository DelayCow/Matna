package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Report;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    @EntityGraph(attributePaths = {"reporter"})  // reporter 엔티티도 함께 로딩
    Optional<Report> findWithReporterByReportNo(Integer reportNo);
}
