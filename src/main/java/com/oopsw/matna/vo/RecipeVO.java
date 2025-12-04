package com.oopsw.matna.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//레시피 목록용 vo
public class RecipeVO {

    private Integer recipeNo;
    private String title;
    private String thumbnailUrl;
    private String writerNickname;
    private String writerProfile;
    private Float averageRating;
//    private Float rating;
    private Integer reviewCount;
    private Integer servings;
    private Integer prepTime;
    private String difficulty;
    private Integer spicyLevel;
    private String summary;
    private String category;

}
