package com.oopsw.matna.service;

import com.oopsw.matna.dao.GroupBuyListDAO;
import com.oopsw.matna.dao.ManagerDAO;
import com.oopsw.matna.dto.ManagerGroupBuyResponse;
import com.oopsw.matna.dto.ManagerIngredientResponse;
import com.oopsw.matna.repository.IngredientRepository;
import com.oopsw.matna.repository.MemberRepository;
import com.oopsw.matna.repository.entity.GroupBuy;
import com.oopsw.matna.repository.entity.Ingredient;
import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.vo.AllGroupBuyListVO;
import com.oopsw.matna.vo.IngredientVO;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Group;
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
    private final ManagerDAO managerDAO;
    private final GroupBuyListDAO groupBuyListDAO;

    //재료 관리
    private ManagerIngredientResponse toManagerIngredientResponse(Ingredient ingredient) {
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
                .map(this::toManagerIngredientResponse)
                .toList();
    }

    public List<ManagerIngredientResponse> getIngredientsByName(String keyword) {
        return ingredientRepository
                .findByIngredientNameContainingAndDelDateIsNull(keyword)
                .stream()
                .map(this::toManagerIngredientResponse)
                .toList();
    }

    public List<ManagerIngredientResponse> getNotApprovedIngredients() {
        return ingredientRepository
                .findAllByApproveDateIsNull()
                .stream()
                .map(this::toManagerIngredientResponse)
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

        return toManagerIngredientResponse(saved);
    }

    public ManagerIngredientResponse removeIngredient(Integer ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("재료를 찾을 수 없습니다."));
        ingredient.setDelDate(LocalDateTime.now());
        return toManagerIngredientResponse(ingredient);
    }

    public ManagerIngredientResponse approveIngredient(Integer ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("재료를 찾을 수 없습니다."));
        ingredient.setApproveDate(LocalDateTime.now());
        return toManagerIngredientResponse(ingredient);
    }

    //공구 관리
    private ManagerGroupBuyResponse toManagerGroupBuyResponse(AllGroupBuyListVO vo) {
        return ManagerGroupBuyResponse.builder()
                .groupBuyNo(vo.getGroupBuyNo())
                .status(vo.getStatus())
                .inDate(vo.getInDate())
                .creatorName(vo.getNickname())
                .title(vo.getTitle())
                .build();
    }

    public List<ManagerGroupBuyResponse> getGroupBuyList(String startDate, String endDate, String status, String title){
        return managerDAO.getAllGroupBuyList(startDate, endDate, status, title)
                .stream()
                .map(this::toManagerGroupBuyResponse)
                .toList();
    }
}
