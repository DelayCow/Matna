package com.oopsw.matna.vo;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@ToString
public class PeriodGroupBuyHomeVO {
    private String title;
    private String nickname;
    private String creatorImageUrl;
    private String groupBuyImageUrl;

    private LocalDateTime inDate;
    private LocalDateTime dueDate;
    private Integer purchasePeriodDays;
    private LocalDateTime finalPurchaseDeadline;

    private Integer minPricePerPerson;
    private Integer maxPricePerPerson;
    private String shareLocation;

    private Integer participants;
    private Integer maxParticipants;

    private Integer groupBuyNo;
    private String ingredientName;
    private Integer periodGroupBuyNo;
}
