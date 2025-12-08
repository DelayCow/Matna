package com.oopsw.matna.service;

import com.oopsw.matna.controller.groupbuy.PeriodRegisterRequest;
import com.oopsw.matna.dao.PeriodGroupBuyDAO;
import com.oopsw.matna.repository.*;
import com.oopsw.matna.repository.entity.*;
import com.oopsw.matna.vo.GroupBuyParticipantVO;
import com.oopsw.matna.vo.PeriodGroupBuyDetailVO;
import com.oopsw.matna.vo.PeriodGroupBuyHomeVO;
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
public class PeriodGroupBuyService {
    private final IngredientRepository ingredientRepository;
    private final MemberRepository memberRepository;
    private final GroupBuyRepository groupBuyRepository;
    private final PeriodGroupBuyRepository periodGroupBuyRepository;
    private final GroupBuyParticipantRepository groupBuyParticipantRepository;
    private final PeriodGroupBuyDAO periodGroupBuyDAO;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final ImageStorageService imageStorageService;


    public List<Ingredient> getIngredientKeyword(String keyword){
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("검색 키워드는 비어있을 수 없습니다.");
        }
        return ingredientRepository.findByIngredientNameContaining(keyword);
    }

    public Ingredient addIngredient(Integer creatorNo, String ingredientName) throws IOException {
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
    public PeriodGroupBuy addPeriodGroupBuy(PeriodRegisterRequest request, MultipartFile thumbnailFile) throws IOException {
        String thumbnailUrl = null;
        if (thumbnailFile == null || thumbnailFile.isEmpty()) {
            throw new IllegalArgumentException("상품 이미지는 필수입니다.");
        }
        thumbnailUrl = imageStorageService.save(thumbnailFile, "groupbuy/thumbnails");

        Ingredient ingredient = ingredientRepository.findById(request.getIngredientNo())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 재료입니다. 재료번호: " + request.getIngredientNo()));
        Member creator = memberRepository.findById(request.getCreatorNo())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. 회원번호: " + request.getCreatorNo()));

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
                        .price(request.getPrice())
                        .quantity(request.getQuantity())
                        .unit(request.getUnit())
                        .feeRate(request.getFeeRate())
                        .imageUrl(thumbnailUrl)
                        .content(request.getContent())
                        .inDate(LocalDateTime.now())
                        .itemSaleUrl(request.getItemSaleUrl())
                        .scrapCount(0)
                        .status("open")
                        .build()
        );
        PeriodGroupBuy periodGroupBuy = periodGroupBuyRepository.save(
                PeriodGroupBuy.builder()
                        .groupBuy(groupBuy)
                        .dueDate(request.getDueDate())
                        .maxParticipants(request.getMaxParticipants())
                        .build()
        );
        return periodGroupBuy;
    }

    @Transactional
    public GroupBuyParticipant addParticipantToPeriodGroupBuy(GroupBuyParticipantVO vo) {
        Member participantMember = memberRepository.findById(vo.getParticipantNo())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. 회원번호: " + vo.getParticipantNo()));
        GroupBuy groupBuy = groupBuyRepository.findById(vo.getGroupBuyNo())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공동구매입니다. 공구번호: " + vo.getGroupBuyNo()));

        Integer price = groupBuy.getPrice();
        Integer feeRate = groupBuy.getFeeRate();
        int initialPaymentPoint = (int) Math.round((price * (1.0 + (feeRate / 100.0))) / 2.0);

        int updatePoint = -initialPaymentPoint;
        if (participantMember.getPoint() + updatePoint < 0) {
            throw new IllegalArgumentException("포인트가 부족합니다. 현재 포인트: " + participantMember.getPoint()
                    + "원, 필요 포인트: " + initialPaymentPoint + "원");
        }

        participantMember.setPoint(participantMember.getPoint() + updatePoint);
        memberRepository.save(participantMember);
        GroupBuyParticipant joinPeriodGroupBuy = groupBuyParticipantRepository.save(
                GroupBuyParticipant.builder()
                        .participant(participantMember)
                        .groupBuy(groupBuy)
                        .participatedDate(LocalDateTime.now())
                        .initialPaymentPoint(initialPaymentPoint)
                        .build()
        );
        return joinPeriodGroupBuy;
    }

    @Transactional
    public void editGroupBuyStatusAndRefund(Integer groupBuyNo) {
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공동구매입니다. 공구번호: " + groupBuyNo));
        PeriodGroupBuy periodGroupBuy = periodGroupBuyRepository.findByGroupBuy(groupBuy);
        if (periodGroupBuy == null) {
            throw new IllegalArgumentException("해당 공동구매의 기간 정보를 찾을 수 없습니다.");
        }

        // 현재 참여자 수 확인
        List<GroupBuyParticipant> participants = groupBuyParticipantRepository.findByGroupBuy(groupBuy);
        int currentParticipantCount = participants.size();
        int maxParticipants = periodGroupBuy.getMaxParticipants();
        LocalDateTime dueDate = periodGroupBuy.getDueDate();

        // 마감 조건 확인
        boolean isMaxParticipantsReached = currentParticipantCount >= (maxParticipants - 1);
        boolean isDueDatePassed = LocalDateTime.now().isAfter(dueDate);

        if (!isMaxParticipantsReached && !isDueDatePassed) {
            throw new IllegalStateException("공동구매를 마감할 수 없습니다. 현재 참여자: " + currentParticipantCount
                    + ", 최대 참여자: " + (maxParticipants - 1) + ", 마감일: " + dueDate);
        }

        groupBuy.setStatus("closed");
        groupBuyRepository.save(groupBuy);

        // 각 참여자의 최종 금액 및 수량 계산 및 환불 처리
        Integer totalPrice = groupBuy.getPrice();
        Integer totalQuantity = groupBuy.getQuantity();
        Integer feeRate = groupBuy.getFeeRate();

        int finalQuantityPerPerson = totalQuantity / currentParticipantCount;
        int finalPaymentPointPerPerson = (int) Math.round(
                (totalPrice * (1.0 + (feeRate / 100.0))) / currentParticipantCount
        );

        for (GroupBuyParticipant participant : participants) {
            participant.setMyQuantity(finalQuantityPerPerson);
            participant.setFinalPaymentPoint(finalPaymentPointPerPerson);
            groupBuyParticipantRepository.save(participant);

            int initialPaymentPoint = participant.getInitialPaymentPoint();
            int refundAmount = initialPaymentPoint - finalPaymentPointPerPerson;

            if (refundAmount > 0) {
                Member member = participant.getParticipant();
                member.setPoint(member.getPoint() + refundAmount);
                memberRepository.save(member);
            }
        }
    }

    @Transactional
    public void editCancelParticipantGroupBuy(Integer groupBuyParticipantNo){
        GroupBuyParticipant groupBuyParticipant = groupBuyParticipantRepository.findById(groupBuyParticipantNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 참여 정보입니다. 참여자번호: " + groupBuyParticipantNo));
        if (groupBuyParticipant.getCancelDate() != null) {
            throw new IllegalStateException("이미 취소된 참여입니다. 취소일: " + groupBuyParticipant.getCancelDate());
        }
        // 공동구매 상태 확인
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
    public void editPeriodCreatorCancelAndRefund(Integer groupBuyNo, String cancelReason) {
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공동구매입니다. 공구번호: " + groupBuyNo));
        if ("closed".equals(groupBuy.getStatus())) {
            throw new IllegalStateException("이미 마감된 공동구매는 취소할 수 없습니다.");
        }
        if ("canceled".equals(groupBuy.getStatus())) {
            throw new IllegalStateException("이미 취소된 공동구매입니다.");
        }

        // 1. GroupBuy 상태 변경 및 취소 사유
        groupBuy.setStatus("canceled");
        groupBuy.setCancelReason(cancelReason);
        groupBuyRepository.save(groupBuy);

        // 2. 모든 참여자 조회
        List<GroupBuyParticipant> participants = groupBuyParticipantRepository.findByGroupBuy(groupBuy);

        // 3. 각 참여자에게 전액 환불 및 취소 일시 설정
        for (GroupBuyParticipant participant : participants) {
            // 이미 취소된 참여자는 스킵
            if (participant.getCancelDate() != null) {
                continue;
            }
            // 환불 처리
            Member member = participant.getParticipant();
            int initialPaymentPoint = participant.getInitialPaymentPoint();
            int currentPoint = member.getPoint();
            member.setPoint(currentPoint + initialPaymentPoint);
            memberRepository.save(member);

            participant.setCancelDate(LocalDateTime.now());
            groupBuyParticipantRepository.save(participant);
        }
    }


    public List<PeriodGroupBuyHomeVO> getPeriodGroupBuyHome(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        periodGroupBuyDAO.updateStatusToClosedIfDueDatePassed(LocalDateTime.now());
        List<PeriodGroupBuyHomeVO> list = periodGroupBuyDAO.selectGroupBuyListForHome(params);
        return list;
    }

    @Transactional
    public Map<String, Object> getPeriodGroupBuyDetail(Integer periodGroupBuyNo) {
        PeriodGroupBuyDetailVO detailVO = periodGroupBuyDAO.selectPeriodGroupBuyDetail(periodGroupBuyNo);
        if (detailVO == null) {
            throw new IllegalArgumentException("존재하지 않는 기간 공동구매입니다. 번호: " + periodGroupBuyNo);
        }

        Integer groupBuyNo = detailVO.getGroupBuyNo();
        if (groupBuyNo == null) {
            throw new IllegalStateException("공동구매를 찾을 수 없습니다.");
        }
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyNo)
                .orElseThrow(() -> new IllegalStateException("GroupBuy 엔티티를 찾을 수 없습니다. 번호: " + groupBuyNo));
        List<Map<String, Object>> participantInfoList = new ArrayList<>();
        List<GroupBuyParticipant> participants =
                groupBuyParticipantRepository.findByGroupBuyAndCancelDateIsNullOrderByParticipatedDateAsc(groupBuy);



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
