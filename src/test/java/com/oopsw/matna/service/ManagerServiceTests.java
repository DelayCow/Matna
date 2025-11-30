package com.oopsw.matna.service;

import com.oopsw.matna.dto.ManagerIngredientResponse;
import com.oopsw.matna.repository.IngredientRepository;
import com.oopsw.matna.repository.entity.Ingredient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@SpringBootTest
public class ManagerServiceTests {
    @Autowired
    private ManagerService managerService;

    @Autowired
    private IngredientRepository ingredientRepository;

    private ManagerIngredientResponse toResponse(Ingredient ingredient) {
        return ManagerIngredientResponse.builder()
                .ingredientId(ingredient.getIngredientNo())       // 엔티티 필드명 확인
                .ingredientName(ingredient.getIngredientName())
                .creatorName(ingredient.getCreator().getNickname())
                .inDate(ingredient.getInDate())
                .approveDate(ingredient.getApproveDate())
                .build();
    }

    @Test
    @Transactional
    public void getIngredients() {
        System.out.println(managerService.getIngredients());
    }

    @Test
    public void searchIngredients() {
        String keyword = "감자";
        System.out.println(managerService.getIngredientsByName(keyword));
    }

    @Test
    public void searchIngredientsByApproveDate() {
        System.out.println(managerService.getNotApprovedIngredients());
    }
}
