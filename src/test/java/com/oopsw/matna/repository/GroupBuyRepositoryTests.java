package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Ingredient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class GroupBuyRepositoryTests {
    @Autowired
    IngredientRepository ingredientRepository;

    @Test
    void searchIngredientKeyword(){
        String keyword = "쌀";
        List<Ingredient> results = ingredientRepository.findByIngredientNameContaining(keyword);
        for (Ingredient ingredient : results) {
            System.out.println(ingredient.getIngredientName());
        }
    }
}
