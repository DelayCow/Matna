package com.oopsw.matna.controller.manager;

import com.oopsw.matna.dto.ManagerIngredientResponse;
import com.oopsw.matna.repository.entity.Ingredient;
import com.oopsw.matna.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagerRestController {
    private final ManagerService managerService;

    @GetMapping("/ingredientManagementt")
    public List<ManagerIngredientResponse> ingredientManagement() {
        List<Ingredient> ingredientList = managerService.getIngredients();
        List<ManagerIngredientResponse> result = ingredientList.stream()
                .map(ingredient -> ManagerIngredientResponse.builder()
                        .id(ingredient.getIngredientNo())
                        .ingredientName(ingredient.getIngredientName())
                        .creatorName()
                        .build()).collect(Collectors.toList());
    }
}
