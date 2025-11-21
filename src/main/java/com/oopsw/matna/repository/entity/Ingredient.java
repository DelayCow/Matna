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
@Table(name = "INGREDIENTS")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_no")
    private Integer ingredientNo;

    @Column(name = "ingredient_name", nullable = false)
    private String ingredientName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_no", nullable = false)
    private Member creator; // FK: MEMBERS.member_no

    @Column(name = "in_date", nullable = false)
    private LocalDateTime inDate;

    @Column(name = "approve_date")
    private LocalDateTime approveDate;

    @Column(name = "del_date")
    private LocalDateTime delDate;

}