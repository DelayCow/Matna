package com.oopsw.matna.controller.ingredient;

import com.oopsw.matna.dto.IngredientResponse;
import com.oopsw.matna.service.IngredientService;
import com.oopsw.matna.vo.IngredientVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class IngredientRestController {
    private final IngredientService ingredientService;

    @GetMapping("/ingredients")
    public List<IngredientResponse> findIngredientByKeyword(@RequestParam String keyword) {
        List<IngredientVO> ingredients = ingredientService.findIngredientByKeyword(keyword);
        return ingredients.stream().map(ingredient -> IngredientResponse.builder()
                .ingredientNo(ingredient.getIngredientNo())
                .ingredientName(ingredient.getIngredientName())
                .build()).collect(Collectors.toList());
    };
}
