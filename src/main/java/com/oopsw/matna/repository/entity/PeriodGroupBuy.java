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
@Table(name = "PERIOD_GROUP_BUYS")
public class PeriodGroupBuy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "period_group_buy_no")
    private Integer periodGroupBuyNo;

    @OneToOne(fetch = FetchType.LAZY) // GROUP_BUYS와 1:1 관계일 가능성이 높음
    @JoinColumn(name = "group_buy_no", nullable = false)
    private GroupBuy groupBuy; // FK: GROUP_BUYS.group_buy_no

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;
}