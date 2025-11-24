package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Integer> {
}
