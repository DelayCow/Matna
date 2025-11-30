package com.oopsw.matna.service;

import com.oopsw.matna.dto.ManagerIngredientResponse;
import com.oopsw.matna.repository.IngredientRepository;
import com.oopsw.matna.repository.MemberRepository;
import com.oopsw.matna.repository.entity.Ingredient;
import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.vo.IngredientVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {
    private final IngredientRepository ingredientRepository;
    private final MemberRepository memberRepository;

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

    public ManagerIngredientResponse addIngredient(Integer creatorId, String ingredientName) {
        // creator 정보 조회
        Member creator = memberRepository.findByMemberNo(creatorId);

        Ingredient ingredient = Ingredient.builder()
                .ingredientName(ingredientName)
                .creator(creator)
                .inDate(LocalDateTime.now())
                .approveDate(LocalDateTime.now())
                .build();

        Ingredient saved = ingredientRepository.save(ingredient);

        return toResponse(saved);
    }

    public ManagerIngredientResponse removeIngredient(Integer ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("재료를 찾을 수 없습니다."));
        ingredient.setDelDate(LocalDateTime.now());
        return toResponse(ingredient);
    }

    public ManagerIngredientResponse approveIngredient(Integer ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("재료를 찾을 수 없습니다."));
        ingredient.setApproveDate(LocalDateTime.now());
        return toResponse(ingredient);
    }

}
