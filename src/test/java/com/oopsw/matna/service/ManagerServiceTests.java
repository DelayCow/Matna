package com.oopsw.matna.service;

import com.oopsw.matna.dto.ManagerIngredientResponse;
import com.oopsw.matna.repository.IngredientRepository;
import com.oopsw.matna.repository.ReportRepository;
import com.oopsw.matna.repository.entity.Ingredient;
import com.oopsw.matna.repository.entity.Report;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;

@SpringBootTest
public class ManagerServiceTests {
    @Autowired
    private ManagerService managerService;

    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private ReportRepository reportRepository;

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
    public void deleteIngredient() {
        Ingredient ingredient = ingredientRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("ingredient가 없습니다."));
        managerService.deleteIngredient(1);
        System.out.println(ingredient);
    }

    @Test
    public void approveIngredient() {
        Ingredient ingredient = ingredientRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("ingredient가 없습니다."));
        managerService.approveIngredient(1);
        System.out.println(ingredient);
    }

    @Test
    public void changeIngredient() {
        managerService.changeIngredient(51, 48);
    }

    //공구 관리
    @Test
    public void getAllGroupBuyList() {
        System.out.println(managerService.getGroupBuyList(
                "2024-11-01",
                "2025-12-30",
                "",
                "open"
        ));
    }

    //신고 관리
    @Test
    public void getReportList(){
        System.out.println(managerService.getReportList(
                LocalDate.of(2025, 11, 19),
                LocalDate.of(2025, 11, 25),
                "WIP",
                "group_buys",
                ""
        ));
    }
    @Test
    public void getReportById() {
        System.out.println(managerService.getReportById(1));
    }

    @Test
    @Transactional
    public void editReport(){
        Report report = reportRepository.findById(6).get();
        managerService.editReportStatus(6, "complete");
        System.out.println(report);
    }

    //유저 관리
    @Test
    public void getMembers(){
        System.out.println(managerService.getMemberList(
                "",
                "",
                "주기"
        ));
    }

    @Test
    public void updateBanDate(){
        managerService.updateBanDate(12, "2");
    }

}
