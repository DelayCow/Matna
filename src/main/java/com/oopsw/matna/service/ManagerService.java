package com.oopsw.matna.service;

import com.oopsw.matna.dto.ManagerIngredientResponse;
import com.oopsw.matna.repository.IngredientRepository;
import com.oopsw.matna.repository.entity.Ingredient;
import com.oopsw.matna.vo.IngredientVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {
    private final IngredientRepository ingredientRepository;

    private ManagerIngredientResponse toResponse(Ingredient ingredient) {
        return ManagerIngredientResponse.builder()
                .ingredientId(ingredient.getIngredientNo())       // 엔티티 필드명 확인
                .ingredientName(ingredient.getIngredientName())
                .creatorName(ingredient.getCreator().getNickname())
                .inDate(ingredient.getInDate())
                .approveDate(ingredient.getApproveDate())
                .build();
    }

    public List<ManagerIngredientResponse> getIngredients() {
        return ingredientRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ManagerIngredientResponse> getIngredientsByName(String keyword) {
        return ingredientRepository
                .findByIngredientNameContainingAndDelDateIsNull(keyword)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ManagerIngredientResponse> getNotApprovedIngredients() {
        return ingredientRepository
                .findAllByApproveDateIsNull()
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
