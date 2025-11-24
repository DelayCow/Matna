package com.oopsw.matna.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeListVO {
    private int recipeNo;
    private String title;
    private Float averageRating;
    private String imageUrl;
    private int reviewCount;
}
