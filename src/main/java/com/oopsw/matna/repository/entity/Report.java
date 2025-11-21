package com.oopsw.matna.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "REPORTS")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_no")
    private Integer reportNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_no", nullable = false)
    private Member reporter; // FK: MEMBERS.member_no

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "reported_date", nullable = false)
    private LocalDateTime reportedDate;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "reason", nullable = false, length = 500)
    private String reason;
}