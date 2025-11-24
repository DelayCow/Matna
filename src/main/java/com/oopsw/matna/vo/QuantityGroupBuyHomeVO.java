package com.oopsw.matna.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class QuantityGroupBuyHomeVO {
    private Integer groupBuyNo;
    private String title;
    private String nickname;
    private String creatorImageUrl;
    private String groupBuyImageUrl;
    private LocalDateTime inDate;
    private String ingredientName;

    private Integer quantity;
    private Integer remainingQty;
    private String unit;
    private Integer pricePerUnit;

    private String shareLocation;
}
