package com.oopsw.matna.service;

import com.oopsw.matna.repository.IngredientRepository;
import com.oopsw.matna.repository.entity.Ingredient;
import com.oopsw.matna.vo.IngredientVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public List<IngredientVO> findIngredientByKeyword(String keyword) {
        List<Ingredient> ingredients = ingredientRepository.findByIngredientNameContaining(keyword);
        return ingredients.stream().map(ingredient -> IngredientVO.builder()
                .ingredientNo(ingredient.getIngredientNo())
                .ingredientName(ingredient.getIngredientName())
                .build()).collect(Collectors.toList());
    };
}
