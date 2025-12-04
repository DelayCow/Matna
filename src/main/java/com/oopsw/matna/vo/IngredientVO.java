package com.oopsw.matna.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IngredientVO {
    private Integer ingredientNo;
    private String ingredientName;
    private String unit;
    private Float amount;
    private Boolean isGroupBuying;
}
