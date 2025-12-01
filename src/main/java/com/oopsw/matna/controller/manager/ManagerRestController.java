package com.oopsw.matna.controller.manager;

import com.oopsw.matna.dto.ManagerGroupBuyResponse;
import com.oopsw.matna.dto.ManagerIngredientResponse;
import com.oopsw.matna.dto.ManagerReportResponse;
import com.oopsw.matna.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagerRestController {
    private final ManagerService managerService;

    //재료 관리
    @GetMapping("/ingredientManagementa")
    public List<ManagerIngredientResponse> ingredientManagement() {
        return managerService.getIngredients();
    }

    @GetMapping("/ingredientManagement/search")
    public List<ManagerIngredientResponse> searchIngredient(@RequestParam String keyword) {
        return managerService.getIngredientsByName(keyword);
    }

    @GetMapping("/ingredientManagement/notApproved")
    public List<ManagerIngredientResponse> getNotApprovedIngredients() {
        return managerService.getNotApprovedIngredients();
    }

    @PostMapping("/ingredientManagement/add")
    public ManagerIngredientResponse addIngredient(@RequestParam Integer creatorId, @RequestParam String ingredientName) {
        return managerService.addIngredient(creatorId, ingredientName);
    }

    @PutMapping("/ingredientManagement/remove")
    public void removeIngredient(@RequestParam Integer ingredientId) {
        managerService.removeIngredient(ingredientId);
    }

    @PutMapping("/ingredientManagement/approve")
    public void approveIngredient(@RequestParam Integer ingredientId) {
        managerService.approveIngredient(ingredientId);
    }

    //공구 관리
    @GetMapping("/groupBuyManagementt")
    public List<ManagerGroupBuyResponse> getGroupBuyList(String startDate, String endDate, String status, String title){
        return managerService.getGroupBuyList(startDate, endDate, status, title);
    }


    @GetMapping("/reportManagementa")
    public List<ManagerReportResponse> getReportList(LocalDate startDate, LocalDate endDate, String status, String reportCase, String keyword){
        return managerService.getReportList(startDate, endDate, status, reportCase, keyword);
    }
}
