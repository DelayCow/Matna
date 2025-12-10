package com.oopsw.matna.service;

import com.oopsw.matna.controller.groupbuy.QuantityRegisterRequest;
import com.oopsw.matna.dao.QuantityGroupBuyDAO;
import com.oopsw.matna.repository.*;
import com.oopsw.matna.repository.entity.*;
import com.oopsw.matna.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuantityGroupBuyService {
    private final QuantityGroupBuyRepository quantityGroupBuyRepository;
    private final IngredientRepository ingredientRepository;
    private final MemberRepository memberRepository;
    private final GroupBuyRepository groupBuyRepository;
    private final GroupBuyParticipantRepository groupBuyParticipantRepository;
    private final QuantityGroupBuyDAO quantityGroupBuyDAO;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final ImageStorageService imageStorageService;

    public List<Ingredient> getIngredientKeyword(String keyword){
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("검색 키워드는 비어있을 수 없습니다.");
        }
        return ingredientRepository.findByIngredientNameContaining(keyword);
    }

    public Ingredient addIngredient(Integer creatorNo, String ingredientName) {
        Member creatorMember = memberRepository.findById(creatorNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. 회원번호: " + creatorNo));
        if (ingredientRepository.existsByIngredientName(ingredientName.trim())) {
            throw new IllegalStateException("이미 존재하는 재료명입니다: " + ingredientName);
        }
        Ingredient ingredient = Ingredient.builder()
                .ingredientName(ingredientName.trim()) //공백제거
                .creator(creatorMember)
                .inDate(LocalDateTime.now())
                .build();

        return ingredientRepository.save(ingredient);
    }

    @Transactional
    public QuantityGroupBuy addQuantityGroupBuy(QuantityRegisterRequest request, MultipartFile thumbnailFile) throws IOException {
        String thumbnailUrl = null;
        if (thumbnailFile == null || thumbnailFile.isEmpty()) {
            throw new IllegalArgumentException("상품 이미지는 필수입니다.");
        }
        thumbnailUrl = imageStorageService.save(thumbnailFile, "groupbuy/thumbnails");

        Ingredient ingredient = ingredientRepository.findById(request.getIngredientNo())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 재료입니다. 재료번호: " + request.getIngredientNo()));
        Member creator = memberRepository.findById(request.getCreatorNo())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. 회원번호: " + request.getCreatorNo()));

        // 단위당 가격 계산: (총 가격 * (1 + 수수료율/100)) / 수량
        Integer totalPrice = request.getPrice();
        Integer feeRate = request.getFeeRate() != null ? request.getFeeRate() : 0;
        Integer quantity = request.getQuantity();
        int pricePerUnit = (int) Math.round((totalPrice * (1.0 + (feeRate / 100.0))) / quantity);

        // GroupBuy 생성 및 저장
        GroupBuy groupBuy = groupBuyRepository.save(
                GroupBuy.builder()
                        .ingredient(ingredient)
                        .creator(creator)
                        .title(request.getTitle().trim())
                        .buyEndDate(request.getBuyEndDate())
                        .shareEndDate(request.getShareEndDate())
                        .shareTime(request.getShareTime())
                        .shareLocation(request.getShareLocation())
                        .shareDetailAddress(request.getShareDetailAddress())
                        .price(totalPrice)
                        .quantity(quantity)
                        .unit(request.getUnit())
                        .feeRate(feeRate)
                        .imageUrl(thumbnailUrl)
                        .content(request.getContent())
                        .inDate(LocalDateTime.now())
                        .itemSaleUrl(request.getItemSaleUrl())
                        .scrapCount(0)
                        .status("open")
                        .build()
        );
        // QuantityGroupBuy 생성 및 저장
        QuantityGroupBuy quantityGroupBuy = quantityGroupBuyRepository.save(
                QuantityGroupBuy.builder()
                        .groupBuy(groupBuy)
                        .myQuantity(request.getMyQuantity())
                        .shareAmount(request.getShareAmount())
                        .pricePerUnit(pricePerUnit)
                        .build()
        );

        return quantityGroupBuy;
    }

    @Transactional
    public GroupBuyParticipant addParticipantToQuantityGroupBuy(GroupBuyParticipantVO vo) {
        Member participantMember = memberRepository.findById(vo.getParticipantNo())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. 회원번호: " + vo.getParticipantNo()));
        GroupBuy groupBuy = groupBuyRepository.findById(vo.getGroupBuyNo())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공동구매입니다. 공구번호: " + vo.getGroupBuyNo()));
        QuantityGroupBuy quantityGroupBuy = quantityGroupBuyRepository.findByGroupBuy(groupBuy);
        if (quantityGroupBuy == null) {
            throw new IllegalArgumentException("수량 공동구매 정보를 찾을 수 없습니다.");
        }

        // 초기 결제 포인트 계산
        Integer price = groupBuy.getPrice();
        Integer feeRate = groupBuy.getFeeRate();
        int initialPaymentPoint = (int) Math.round((price * (1.0 + (feeRate / 100.0))) / 2.0);

        // 포인트 차감 처리
        int updatePoint = -initialPaymentPoint;
        if (participantMember.getPoint() + updatePoint < 0) {
            throw new IllegalArgumentException("포인트가 부족합니다. 현재 포인트: " + participantMember.getPoint()
                    + "원, 필요 포인트: " + initialPaymentPoint + "원");
        }

        participantMember.setPoint(participantMember.getPoint() + updatePoint);
        memberRepository.save(participantMember);
        GroupBuyParticipant joinQuantityGroupBuy = groupBuyParticipantRepository.save(
                GroupBuyParticipant.builder()
                        .participant(participantMember)
                        .groupBuy(groupBuy)
                        .participatedDate(LocalDateTime.now())
                        .initialPaymentPoint(initialPaymentPoint)
                        .build()
        );

        // 수량 충족 여부 확인 및 상태 업데이트
        List<GroupBuyParticipant> allParticipants = groupBuyParticipantRepository.findByGroupBuy(groupBuy);
        int currentSharedQuantity = 0;
        for (GroupBuyParticipant participant : allParticipants) {
            // 취소되지 않은 참여자만 카운트
            if (participant.getCancelDate() == null && participant.getMyQuantity() != null) {
                currentSharedQuantity += participant.getMyQuantity();
            }
        }
        // 개설자 부담 수량
        int initialMyQuantity = quantityGroupBuy.getMyQuantity();
        // 총 필요 수량
        int totalQuantity = groupBuy.getQuantity();
        // 수량 충족 확인
        boolean isQuantitySatisfied = (currentSharedQuantity + initialMyQuantity >= totalQuantity);
        if (isQuantitySatisfied) {
            groupBuy.setStatus("closed");
            groupBuyRepository.save(groupBuy);
        }

        return joinQuantityGroupBuy;
    }

    @Transactional
    public void editCancelParticipantGroupBuy(Integer groupBuyParticipantNo){
        GroupBuyParticipant groupBuyParticipant = groupBuyParticipantRepository.findById(groupBuyParticipantNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 참여 정보입니다. 참여자번호: " + groupBuyParticipantNo));
        if (groupBuyParticipant.getCancelDate() != null) {
            throw new IllegalStateException("이미 취소된 참여입니다. 취소일: " + groupBuyParticipant.getCancelDate());
        }
        GroupBuy groupBuy = groupBuyParticipant.getGroupBuy();
        if ("closed".equals(groupBuy.getStatus())) {
            throw new IllegalStateException("마감된 공동구매는 취소할 수 없습니다.");
        }

        groupBuyParticipant.setCancelDate(LocalDateTime.now());
        groupBuyParticipantRepository.save(groupBuyParticipant);

        Member participant = groupBuyParticipant.getParticipant();
        int initialPaymentPoint = groupBuyParticipant.getInitialPaymentPoint();
        int currentPoint = participant.getPoint();
        int newPoint = currentPoint + initialPaymentPoint;

        participant.setPoint(newPoint);
        memberRepository.save(participant);
    }

    @Transactional
    public QuantityGroupBuy editForceCloseQuantityGroupBuy(Integer groupBuyNo, Integer creatorNo) {
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공동구매입니다. 공구번호: " + groupBuyNo));
        if (!groupBuy.getCreator().getMemberNo().equals(creatorNo)) {
            throw new IllegalArgumentException("개설자만 강제 마감할 수 있습니다.");
        }
        if ("closed".equals(groupBuy.getStatus())) {
            throw new IllegalStateException("이미 마감된 공동구매입니다.");
        }
        // QuantityGroupBuy 조회
        QuantityGroupBuy quantityGroupBuy = quantityGroupBuyRepository.findByGroupBuy(groupBuy);
        if (quantityGroupBuy == null) {
            throw new IllegalArgumentException("수량 공동구매 정보를 찾을 수 없습니다.");
        }

        // 현재 수량 계산
        List<GroupBuyParticipant> participants = groupBuyParticipantRepository.findByGroupBuy(groupBuy);
        int currentSharedQuantity = 0;
        for (GroupBuyParticipant participant : participants) {
            if (participant.getCancelDate() == null && participant.getMyQuantity() != null) {
                currentSharedQuantity += participant.getMyQuantity();
            }
        }

        // 현재 개설자 부담 수량
        int initialMyQuantity = quantityGroupBuy.getMyQuantity();
        // 총 필요 수량
        int totalQuantity = groupBuy.getQuantity();
        // 남은 수량 계산
        int remainingQuantity = totalQuantity - (currentSharedQuantity + initialMyQuantity);
        // 수량이 이미 충족된 경우
        if (remainingQuantity <= 0) {
            throw new IllegalStateException("이미 수량이 충족되었습니다. 강제 마감이 필요하지 않습니다.");
        }

        // 개설자가 남은 수량 추가 부담
        quantityGroupBuy.setMyQuantity(initialMyQuantity + remainingQuantity);
        quantityGroupBuyRepository.save(quantityGroupBuy);
        groupBuy.setStatus("closed");
        groupBuyRepository.save(groupBuy);

        return quantityGroupBuy;
    }


    public List<QuantityGroupBuyHomeVO> getQuantityGroupBuyHome(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        List<QuantityGroupBuyHomeVO> list = quantityGroupBuyDAO.selectQuantityGroupBuyHomeList(params);
        return list;
    }

    @Transactional
    public Map<String, Object> getQuantityGroupBuyDetail(Integer quantityGroupBuyNo) {
        QuantityGroupBuyDetailVO detailVO = quantityGroupBuyDAO.selectQuantityGroupBuyDetail(quantityGroupBuyNo);
        if (detailVO == null) {
            throw new IllegalArgumentException("존재하지 않는 수량 공동구매입니다. 번호: " + quantityGroupBuyNo);
        }
        Integer groupBuyNo = detailVO.getGroupBuyNo();
        if (groupBuyNo == null) {
            throw new IllegalStateException("공동구매를 찾을 수 없습니다.");
        }
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyNo)
                .orElseThrow(() -> new IllegalStateException("GroupBuy 엔티티를 찾을 수 없습니다. 번호: "+ groupBuyNo));

//        List<Map<String, Object>> participantInfoList = quantityGroupBuyDAO.selectQuantityGroupBuyParticipants(quantityGroupBuyNo);
        List<Map<String, Object>> participantInfoList = new ArrayList<>();
        List<GroupBuyParticipant> participants = groupBuyParticipantRepository.findByGroupBuyAndCancelDateIsNullOrderByParticipatedDateAsc(groupBuy);
        if (participants != null && !participants.isEmpty()) {
            for (GroupBuyParticipant gbp : participants) {
                if (gbp == null) continue;

                Member member = gbp.getParticipant();
                if (member == null) continue;

                Map<String, Object> participantInfo = new HashMap<>();
                participantInfo.put("nickname", member.getNickname() != null ? member.getNickname() : "익명");
                participantInfo.put("profileUrl", member.getImageUrl() != null ? member.getImageUrl() : "");
                participantInfo.put("participatedDate", gbp.getParticipatedDate());
                participantInfoList.add(participantInfo);
            }
        }

        List<Map<String, Object>> recipeInfoList = new ArrayList<>();
        Integer ingredientNo = detailVO.getIngredientNo();
        if (ingredientNo != null) {
            List<RecipeIngredient> recipeIngredients =
                    recipeIngredientRepository.findByIngredientIngredientNoOrderByRecipeInDateDesc(ingredientNo);

            if (recipeIngredients != null && !recipeIngredients.isEmpty()) {
                for (RecipeIngredient ri : recipeIngredients) {
                    if (ri == null) continue;

                    Recipe recipe = ri.getRecipe();
                    if (recipe == null) continue;

                    Member author = recipe.getAuthor();

                    Map<String, Object> recipeInfo = new HashMap<>();
                    recipeInfo.put("recipeNo", recipe.getRecipeNo());
                    recipeInfo.put("title", recipe.getTitle() != null ? recipe.getTitle() : "제목 없음");
                    recipeInfo.put("imageUrl", recipe.getImageUrl() != null ? recipe.getImageUrl() : "");
                    recipeInfo.put("authorNickname", author != null && author.getNickname() != null ? author.getNickname() : "익명");
                    recipeInfo.put("inDate", recipe.getInDate());
                    recipeInfoList.add(recipeInfo);
                }
            }
        }

        // 4. 통합 Map 생성
        Map<String, Object> response = new HashMap<>();
        response.put("groupBuyDetail", detailVO);
        response.put("participants", participantInfoList);
        response.put("recipes", recipeInfoList);

        return response;
    }
}
