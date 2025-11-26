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
    private int purchasePeriodDays;
    private LocalDateTime finalPurchaseDeadline;

    private int minPricePerPerson;
    private int maxPricePerPerson;
    private String shareLocation;

    private int participants;
    private int maxParticipants;

    private Long groupBuyNo;
    private String ingredientName;
}
