package com.oopsw.matna.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class MypageResponse {
    private int recipeNo;
    private String title;
    private Float averageRating;
    private String imageUrl;
    private int reviewCount;
}
