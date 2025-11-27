package com.oopsw.matna.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class RecipeListResponse {
    private int id;
    private String title;
    private Float rating;
    private String image;
    private int reviewCount;
    private String difficulty;
    private Integer time;
    private Integer serving;
    private Integer spicy;
}
