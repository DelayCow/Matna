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
@Table(name = "GROUP_BUY_PARTICIPANT")
public class GroupBuyParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_participant_no")
    private Integer groupParticipantNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_no", nullable = false)
    private Member participantNo; // FK: MEMBERS.member_no

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_buy_no", nullable = false)
    private GroupBuy groupBuyNo; // FK: GROUP_BUYS.group_buy_no

    @Column(name = "my_quantity")
    private Integer myQuantity;

    @Column(name = "participated_date", nullable = false)
    private LocalDateTime participatedDate;

    @Column(name = "receive_date")
    private LocalDateTime receiveDate;

    @Column(name = "initial_payment_point", nullable = false)
    private Integer initialPaymentPoint;

    @Column(name = "final_payment_point")
    private Integer finalPaymentPoint;

    @Column(name = "cancel_date")
    private LocalDateTime cancelDate;
}