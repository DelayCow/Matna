package com.oopsw.matna.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeStepVO {

    private Integer recipeStepNo;
    private Integer recipeNo;    //? 필요 한가?
    private String imageUrl;
    private String content;
    private Integer stepOrder;

}
