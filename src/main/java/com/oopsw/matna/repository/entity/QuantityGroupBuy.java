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
@Table(name = "QUANTITY_GROUP_BUYS")
public class QuantityGroupBuy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quantity_group_buy_no")
    private Integer quantityGroupBuyNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_buy_no", nullable = false)
    private GroupBuy groupBuy; // FK: GROUP_BUYS.group_buy_no

    @Column(name = "my_quantity", nullable = false)
    private Integer myQuantity;

    @Column(name = "share_amount", nullable = false)
    private Integer shareAmount;

    @Column(name = "price_per_unit", nullable = false)
    private Integer pricePerUnit;
}