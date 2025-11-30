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

    @Test
    public void addIngredient() {
        System.out.println(managerService.addIngredient(1, "애호박"));
    }

    @Test
    public void removeIngredient() {
        System.out.println(managerService.removeIngredient(1));
    }

    @Test
    public void approveIngredient() {
        System.out.println(managerService.approveIngredient(50));
    }

    //공구 관리
    @Test
    public void getAllGroupBuyList() {
        System.out.println(managerService.getGroupBuyList(
                "2024-11-01",
                "2025-11-30",
                null,
                "아"
        ));
    }
}
