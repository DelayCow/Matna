package com.oopsw.matna.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailIngredientVO {

    private String name;
    private Float amount;
    private String unit;

}
