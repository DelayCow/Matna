package com.oopsw.matna.controller.manager;

import com.oopsw.matna.dto.ManagerGroupBuyResponse;
import com.oopsw.matna.dto.ManagerIngredientResponse;
import com.oopsw.matna.repository.entity.GroupBuy;
import com.oopsw.matna.repository.entity.Ingredient;
import com.oopsw.matna.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @PostMapping("/ingredientManagement")
    public ManagerIngredientResponse addIngredient(@RequestParam Integer creatorId, @RequestParam String ingredientName) {
        return managerService.addIngredient(creatorId, ingredientName);
    }

    @DeleteMapping("/ingredientManagement")
    public ManagerIngredientResponse removeIngredient(@RequestParam Integer ingredientId) {
        return managerService.removeIngredient(ingredientId);
    }

    @PutMapping("/ingredientManagement")
    public ManagerIngredientResponse approveIngredient(@RequestParam Integer ingredientId) {
        return managerService.approveIngredient(ingredientId);
    }

    //공구 관리
    @GetMapping("/groupBuyManagementt")
    public List<ManagerGroupBuyResponse> getGroupBuyList(String startDate, String endDate, String status, String title){
        return managerService.getGroupBuyList(startDate, endDate, status, title);
    }

    //
}
