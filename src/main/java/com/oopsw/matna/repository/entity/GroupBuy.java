package com.oopsw.matna.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GROUP_BUYS")
public class GroupBuy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_buy_no")
    private Integer groupBuyNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_no", nullable = false)
    private Ingredient ingredient; // FK: INGREDIENTS.ingredient_no

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_no", nullable = false)
    private Member creator; // FK: MEMBERS.member_no

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "buy_end_date", nullable = false)
    private Integer buyEndDate; //모집 마감 후 buyEndDate일 후

    @Column(name = "share_end_date", nullable = false)
    private Integer shareEndDate; //수령일 포함 shareEndDate일 후

    @Column(name = "share_location", nullable = false, length = 500)
    private String shareLocation;

    @Column(name = "share_time", nullable = false)
    private LocalTime shareTime;

    @Column(name = "share_detail_address", nullable = false, length = 500)
    private String shareDetailAddress;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit", nullable = false, length = 10)
    private String unit;

    @Column(name = "fee_rate", nullable = false)
    private Integer feeRate;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "in_date", nullable = false)
    private LocalDateTime inDate;

    @Column(name = "item_sale_url", length = 1000)
    private String itemSaleUrl;

    @Column(name = "scrap_count", nullable = false)
    private Integer scrapCount;

    @Column(name = "status", nullable = false, length = 30)
    private String status; // 예: "OPEN", "CLOSED", "SHARING"

    @Column(name = "receipt_image_url", length = 1000)
    private String receiptImageUrl;

    @Column(name = "buy_date")
    private LocalDateTime buyDate;

    @Column(name = "payment_note")
    private String paymentNote;

    @Column(name = "arrival_image_url", length = 1000)
    private String arrivalImageUrl;

    @Column(name = "arrival_date")
    private LocalDateTime arrivalDate;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    // Getter, Setter, Constructor 등 생략
}