package com.oopsw.matna.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@SpringBootTest
public class RecipeServiceTests {
    @Autowired
    private RecipeService recipeService;

    @Test
    public void getRecipeList() {
        System.out.println("리뷰순 3개" + recipeService.getRecipeList(null, null, PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "reviewCount"))));
        System.out.println("최신순 3개 + 맵기 0단계" + recipeService.getRecipeList(0, null, PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "inDate"))));
    }



}
