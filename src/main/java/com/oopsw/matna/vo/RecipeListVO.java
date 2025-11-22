package com.oopsw.matna.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RecipeListVO {
    private int recipeNo;
    private String title;
    private Float averageRating;
    private String imageUrl;
    private int reviewCount;
}
