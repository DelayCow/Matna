package com.oopsw.matna.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MEMBER_REPORTS")
public class MemberReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_report_no")
    private Integer memberReportNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_no", nullable = false)
    private Report report; // FK: REPORTS.report_no

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_no", nullable = false)
    private Member targetMember;
}