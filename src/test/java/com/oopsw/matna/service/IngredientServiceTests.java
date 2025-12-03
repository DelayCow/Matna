package com.oopsw.matna.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class IngredientServiceTests {
    @Autowired
    private IngredientService ingredientService;

    @Test
    void findIngredient(){
        System.out.println(ingredientService.findIngredientByKeyword("고기"));
    }
}
