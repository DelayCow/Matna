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
@Table(name = "GROUP_BUY_REPORTS")
public class GroupBuyReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_buy_report_no")
    private Integer groupBuyReportNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_no", nullable = false)
    private Report report; // FK: REPORTS.report_no

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_buy_no", nullable = false)
    private GroupBuy groupBuy;
}