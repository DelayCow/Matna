package com.oopsw.matna.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeListVO {
    private int recipeNo;
    private String title;
    private Float averageRating;
    private String imageUrl;
    private int reviewCount;
}
