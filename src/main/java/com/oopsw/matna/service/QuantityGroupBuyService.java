package com.oopsw.matna.service;

import com.oopsw.matna.controller.groupbuy.GroupBuyParticipantRequest;
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
import java.util.*;

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
    public GroupBuyParticipant addParticipantToQuantityGroupBuy(GroupBuyParticipantRequest request) {

        Member participantMember = memberRepository.findById(request.getParticipantNo())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. 회원번호: " + request.getParticipantNo()));

        GroupBuy groupBuy = groupBuyRepository.findById(request.getGroupBuyNo())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공동구매입니다. 공구번호: " + request.getGroupBuyNo()));

        QuantityGroupBuy quantityGroupBuy = quantityGroupBuyRepository.findByGroupBuy(groupBuy);
        if (quantityGroupBuy == null) {
            throw new IllegalArgumentException("수량공구 정보를 찾을 수 없습니다.");
        }

        if (groupBuy.getCreator().getMemberNo().equals(participantMember.getMemberNo())) {
            throw new IllegalArgumentException("공동구매 개설자는 본인의 공구에 참여할 수 없습니다.");
        }

        boolean alreadyParticipated = groupBuyParticipantRepository
                .findByGroupBuy_GroupBuyNoAndParticipant_MemberNoAndCancelDateIsNull(
                        request.getGroupBuyNo(), request.getParticipantNo())
                .isPresent();

        if (alreadyParticipated) {
            throw new IllegalArgumentException("이미 해당 공동구매에 참여하셨습니다.");
        }

        if ("closed".equals(groupBuy.getStatus())) {
            throw new IllegalArgumentException("마감된 공동구매에는 참여할 수 없습니다.");
        }
        if ("canceled".equals(groupBuy.getStatus())) {
            throw new IllegalArgumentException("취소된 공동구매에는 참여할 수 없습니다.");
        }

        int shareAmount = quantityGroupBuy.getShareAmount();
        Integer participantQuantity = request.getMyQuantity();

        if (participantQuantity == null || participantQuantity <= 0) {
            throw new IllegalArgumentException("참여 수량을 입력해주세요.");
        }

        if (participantQuantity % shareAmount != 0) {
            throw new IllegalArgumentException(
                    "참여 수량은 " + shareAmount + "개/g 단위로만 선택 가능합니다. 입력한 수량: " + participantQuantity);
        }

        // 초기 결제 금액 계산: (참여 수량 / shareAmount) * pricePerUnit
        int pricePerUnit = quantityGroupBuy.getPricePerUnit();
        int shareUnits = participantQuantity / shareAmount;
        int initialPaymentPoint = shareUnits * pricePerUnit;

        // 포인트 잔액 확인
        int updatePoint = -initialPaymentPoint;
        if (participantMember.getPoint() + updatePoint < 0) {
            throw new IllegalArgumentException("포인트가 부족합니다. 현재 포인트: " + participantMember.getPoint()
                    + "원, 필요 포인트: " + initialPaymentPoint + "원");
        }

        // 포인트 차감
        participantMember.setPoint(participantMember.getPoint() + updatePoint);
        memberRepository.save(participantMember);

        GroupBuyParticipant joinQuantityGroupBuy = groupBuyParticipantRepository.save(
                GroupBuyParticipant.builder()
                        .participant(participantMember)
                        .groupBuy(groupBuy)
                        .participatedDate(LocalDateTime.now())
                        .myQuantity(participantQuantity)
                        .initialPaymentPoint(initialPaymentPoint)
                        .build()
        );

        // ===== 수량 충족 확인 및 자동 마감 로직 =====

        int totalQuantity = groupBuy.getQuantity();
        int creatorQuantity = quantityGroupBuy.getMyQuantity();
        List<GroupBuyParticipant> allParticipants = groupBuyParticipantRepository.findByGroupBuy(groupBuy);
        int totalSharedQuantity = allParticipants.stream()
                .filter(p -> p.getCancelDate() == null)
                .mapToInt(GroupBuyParticipant::getMyQuantity)
                .sum();

        boolean isQuantityMet = (creatorQuantity + totalSharedQuantity >= totalQuantity);
        if (isQuantityMet) {
            groupBuy.setStatus("closed");
            groupBuyRepository.save(groupBuy);
        }

        return joinQuantityGroupBuy;
    }


    @Transactional
    public void editModifyMyQuantity(Integer groupBuyParticipantNo, Integer currentMemberNo, Integer newQuantity) {

        GroupBuyParticipant groupBuyParticipant = groupBuyParticipantRepository.findById(groupBuyParticipantNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 참여 정보입니다. 참여자번호: " + groupBuyParticipantNo));

        if (!groupBuyParticipant.getParticipant().getMemberNo().equals(currentMemberNo)) {
            throw new IllegalArgumentException("본인의 참여 내역만 수정할 수 있습니다.");
        }

        if (groupBuyParticipant.getCancelDate() != null) {
            throw new IllegalArgumentException("취소된 참여는 수정할 수 없습니다. 취소일: " + groupBuyParticipant.getCancelDate());
        }

        GroupBuy groupBuy = groupBuyParticipant.getGroupBuy();
        if ("closed".equals(groupBuy.getStatus())) {
            throw new IllegalArgumentException("마감된 공동구매는 수정할 수 없습니다.");
        }
        if ("canceled".equals(groupBuy.getStatus())) {
            throw new IllegalArgumentException("취소된 공동구매는 수정할 수 없습니다.");
        }

        QuantityGroupBuy quantityGroupBuy = quantityGroupBuyRepository.findByGroupBuy(groupBuy);
        if (quantityGroupBuy == null) {
            throw new IllegalArgumentException("수량공구 정보를 찾을 수 없습니다.");
        }

        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("수정할 수량을 입력해주세요.");
        }

        int shareAmount = quantityGroupBuy.getShareAmount();
        if (newQuantity % shareAmount != 0) {
            throw new IllegalArgumentException(
                    "수정 수량은 " + shareAmount + "개/g 단위로만 선택 가능합니다. 입력한 수량: " + newQuantity);
        }

        // 수정 전 데이터
        int beforeInitialPayment = groupBuyParticipant.getInitialPaymentPoint();
        Member participantMember = groupBuyParticipant.getParticipant();
        int beforePoint = participantMember.getPoint();

        // 새로운 결제 금액 계산
        int pricePerUnit = quantityGroupBuy.getPricePerUnit();
        int shareUnits = newQuantity / shareAmount;
        int newPayment = shareUnits * pricePerUnit;
        int modifyPoint = newPayment - beforeInitialPayment; // 양수: 추가 지불, 음수: 환불

        // 포인트 계산 및 잔액 확인
        int newPoint = beforePoint - modifyPoint;
        if (newPoint < 0) {
            throw new IllegalArgumentException(
                    "포인트가 부족합니다. 현재 포인트: " + beforePoint + "P, 필요 포인트: " + modifyPoint + "P");
        }

        groupBuyParticipant.setMyQuantity(newQuantity);
        groupBuyParticipant.setInitialPaymentPoint(newPayment);
        groupBuyParticipantRepository.save(groupBuyParticipant);

        participantMember.setPoint(newPoint);
        memberRepository.save(participantMember);
    }

    @Transactional
    public void editCancelParticipantGroupBuy(Integer groupBuyParticipantNo, Integer currentMemberNo){
        GroupBuyParticipant groupBuyParticipant = groupBuyParticipantRepository.findById(groupBuyParticipantNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 참여 정보입니다. 참여자번호: " + groupBuyParticipantNo));

        if (!groupBuyParticipant.getParticipant().getMemberNo().equals(currentMemberNo)) {
            throw new IllegalStateException("본인의 참여 내역만 취소할 수 있습니다.");
        }

        if (groupBuyParticipant.getCancelDate() != null) {
            throw new IllegalStateException("이미 취소된 참여입니다. 취소일: " + groupBuyParticipant.getCancelDate());
        }

        GroupBuy groupBuy = groupBuyParticipant.getGroupBuy();
        if ("closed".equals(groupBuy.getStatus())) {
            throw new IllegalStateException("마감된 공동구매는 취소할 수 없습니다.");
        }

        groupBuyParticipant.setCancelDate(LocalDateTime.now());
        groupBuyParticipantRepository.save(groupBuyParticipant);

        // 포인트 환불
        Member participant = groupBuyParticipant.getParticipant();
        int initialPaymentPoint = groupBuyParticipant.getInitialPaymentPoint();
        int currentPoint = participant.getPoint();
        int newPoint = currentPoint + initialPaymentPoint;

        participant.setPoint(newPoint);
        memberRepository.save(participant);
    }

    @Transactional
    public QuantityGroupBuy editForcedCreatorAndStatusToClosed(Integer groupBuyNo, Integer currentMemberNo) {
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공동구매입니다. 공구번호: " + groupBuyNo));

        QuantityGroupBuy quantityGroupBuy = quantityGroupBuyRepository.findByGroupBuy(groupBuy);

        if (quantityGroupBuy == null) {
            throw new IllegalArgumentException("수량 공동구매 상세 정보를 찾을 수 없습니다. 공구번호: " + groupBuyNo);
        }
        if (!groupBuy.getCreator().getMemberNo().equals(currentMemberNo)) {
            throw new IllegalStateException("공동구매 개설자만 중단할 수 있습니다.");
        }

        if ("closed".equals(groupBuy.getStatus())) {
            throw new IllegalStateException("이미 마감된 공동구매는 취소할 수 없습니다.");
        }
        if ("canceled".equals(groupBuy.getStatus())) {
            throw new IllegalStateException("이미 취소된 공동구매입니다.");
        }

        List<GroupBuyParticipant> participants = groupBuyParticipantRepository.findByGroupBuy(groupBuy);
        int currentSharedQuantity = 0;
        for (GroupBuyParticipant participant : participants) {
            // 참여 취소되지 않았고, 수량이 null이 아닌 경우만 합산
            if (participant.getCancelDate() == null && participant.getMyQuantity() != null) {
                currentSharedQuantity += participant.getMyQuantity();
            }
        }

        // 현재 개설자 부담 수량 (이제 quantityGroupBuy가 초기화되어 사용 가능)
        int creatorCurrentQuantity = quantityGroupBuy.getMyQuantity();
        // 총 필요 수량
        int totalQuantity = groupBuy.getQuantity();
        // 남은 수량 계산
        int remainingQuantity = totalQuantity - (currentSharedQuantity + creatorCurrentQuantity);

        if (remainingQuantity <= 0) {
            throw new IllegalStateException("이미 수량이 충족되었습니다. 강제 마감이 필요하지 않습니다.");
        }
        int newCreatorQuantity = creatorCurrentQuantity + remainingQuantity;


        quantityGroupBuy.setMyQuantity(newCreatorQuantity);
        quantityGroupBuyRepository.save(quantityGroupBuy);
        groupBuy.setStatus("closed");
        groupBuyRepository.save(groupBuy);

        return quantityGroupBuy;
    }

    @Transactional
    public void editQuantityCreatorCancelAndRefund(Integer groupBuyNo, Integer currentMemberNo, String cancelReason) {
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공동구매입니다. 공구번호: " + groupBuyNo));

        // 개설자 확인
        if (!groupBuy.getCreator().getMemberNo().equals(currentMemberNo)) {
            throw new IllegalStateException("공동구매 개설자만 중단할 수 있습니다.");
        }

        if ("closed".equals(groupBuy.getStatus())) {
            throw new IllegalStateException("이미 마감된 공동구매는 취소할 수 없습니다.");
        }
        if ("canceled".equals(groupBuy.getStatus())) {
            throw new IllegalStateException("이미 취소된 공동구매입니다.");
        }

        groupBuy.setStatus("canceled");
        groupBuy.setCancelReason(cancelReason);
        groupBuyRepository.save(groupBuy);

        List<GroupBuyParticipant> participants = groupBuyParticipantRepository.findByGroupBuy(groupBuy);

        for (GroupBuyParticipant participant : participants) {
            if (participant.getCancelDate() != null) {
                continue;
            }
            Member member = participant.getParticipant();
            int initialPaymentPoint = participant.getInitialPaymentPoint();
            int currentPoint = member.getPoint();
            member.setPoint(currentPoint + initialPaymentPoint);
            memberRepository.save(member);

            participant.setCancelDate(LocalDateTime.now());
            groupBuyParticipantRepository.save(participant);
        }
    }



    public List<QuantityGroupBuyHomeVO> getQuantityGroupBuyHome(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        List<QuantityGroupBuyHomeVO> list = quantityGroupBuyDAO.selectQuantityGroupBuyHomeList(params);
        return list;
    }

    // [수정] 파라미터에 currentMemberNo 추가
    // [중요] 파라미터에 Integer currentMemberNo가 반드시 있어야 합니다!
    @Transactional
    public Map<String, Object> getQuantityGroupBuyDetail(Integer quantityGroupBuyNo, Integer currentMemberNo) {

        // 1. 기본 상세 정보 조회
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



        if (currentMemberNo != null) {

            Optional<GroupBuyParticipant> myPartOpt = groupBuyParticipantRepository
                    .findByGroupBuy_GroupBuyNoAndParticipant_MemberNoAndCancelDateIsNull(groupBuyNo, currentMemberNo);


            if (myPartOpt.isPresent()) {
                GroupBuyParticipant myPart = myPartOpt.get(); // 껍질(Optional) 까기
                detailVO.setMyQuantity(myPart.getMyQuantity());
                detailVO.setGroupParticipantNo(myPart.getGroupParticipantNo());
            } else {

                detailVO.setMyQuantity(0);
                detailVO.setGroupParticipantNo(null);
            }
        } else {

            detailVO.setMyQuantity(0);
            detailVO.setGroupParticipantNo(null);
        }



        // 2. 전체 참여자 리스트 조회 (기존 로직 유지)
        List<Map<String, Object>> participantInfoList = new ArrayList<>();
        List<GroupBuyParticipant> participants = groupBuyParticipantRepository
                .findByGroupBuyAndCancelDateIsNullOrderByParticipatedDateAsc(groupBuy);

        if (participants != null && !participants.isEmpty()) {
            for (GroupBuyParticipant gbp : participants) {
                if (gbp == null) continue;
                Member member = gbp.getParticipant();
                if (member == null) continue;

                Map<String, Object> participantInfo = new HashMap<>();
                participantInfo.put("groupParticipantNo", gbp.getGroupParticipantNo());
                participantInfo.put("memberNo", member.getMemberNo());
                participantInfo.put("nickname", member.getNickname() != null ? member.getNickname() : "익명");
                participantInfo.put("profileUrl", member.getImageUrl() != null ? member.getImageUrl() : "");
                participantInfo.put("myQuantity", gbp.getMyQuantity());
                participantInfo.put("participatedDate", gbp.getParticipatedDate());
                participantInfoList.add(participantInfo);
            }
        }

        // 3. 레시피 정보 조회 (기존 로직 유지)
        List<Map<String, Object>> recipeInfoList = new ArrayList<>();
        Integer ingredientNo = detailVO.getIngredientNo();

        if (ingredientNo != null) {
            List<RecipeIngredient> recipeIngredients = recipeIngredientRepository
                    .findByIngredientIngredientNoOrderByRecipeInDateDesc(ingredientNo);

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

        // 4. 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("groupBuyDetail", detailVO);
        response.put("participants", participantInfoList);
        response.put("recipes", recipeInfoList);

        return response;
    }
}
