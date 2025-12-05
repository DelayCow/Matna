package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@Transactional
public class QuantityGroupBuyRepositoryTests {
    @Autowired
    IngredientRepository ingredientRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    GroupBuyRepository groupBuyRepository;
    @Autowired
    QuantityGroupBuyRepository quantityGroupBuyRepository;
    @Autowired
    GroupBuyParticipantRepository groupBuyParticipantRepository;

    @Test
    void getSearchIngredientKeywordTest() {
        String keyword = "쌀";
        List<Ingredient> results = ingredientRepository.findByIngredientNameContaining(keyword);
        for (Ingredient ingredient : results) {
            System.out.println(ingredient.getIngredientName());
        }
    }

    @Test
    void addIngredientTest() {
        Member creatorMember = memberRepository.findById(5).get();
        Ingredient newIngredient = ingredientRepository.save(
                Ingredient.builder()
                        .ingredientName("빵")
                        .creator(creatorMember)
                        .inDate(LocalDateTime.now())
                        .build());
        System.out.println(newIngredient.getIngredientName());
    }

    @Test
    void addQuantityGroupBuyTest() { // testAddPeriodGroupBuy -> testAddQuantityGroupBuy 변경
        Ingredient ingredientNo = ingredientRepository.findById(45).get();
        Member creatorMember = memberRepository.findById(16).get();

        // 1. GroupBuy 기본 정보 저장 (수량 관련 필드 포함)
        GroupBuy newGroupBuy = groupBuyRepository.save(
                GroupBuy.builder()
                        .ingredient(ingredientNo) //양배추
                        .creator(creatorMember)
                        .title("양배추 4통 같이 나눌분")
                        .buyEndDate(3)
                        .shareEndDate(2)
                        .shareTime(LocalTime.parse("18:00"))
                        .shareLocation("서울 금천구 가산디지털1로 70")
                        .shareDetailAddress("호서대벤처 1층입구")
                        .price(12000)
                        .quantity(4)
                        .unit("개")
                        .feeRate(0)
                        .imageUrl("http://example.com/image_path/cabbage.jpg")
                        .content("양배추가 저렴한데 너무 많아요 같이사실분")
                        .inDate(LocalDateTime.now())
                        .itemSaleUrl("http://sale.site/item/12345")
                        .scrapCount(0)
                        .status("open")
                        .build()
        );

        QuantityGroupBuy newQuantityGroupBuy = quantityGroupBuyRepository.save(
                QuantityGroupBuy.builder()
                        .groupBuy(newGroupBuy)
                        .myQuantity(1)
                        .shareAmount(1)
                        .pricePerUnit(3000)
                        .build()
        );

        System.out.println(newGroupBuy.getTitle());
        System.out.println("개설자 부담 수량: " + newQuantityGroupBuy.getMyQuantity() + "개/g");
        System.out.println("나눔 단위: " + newQuantityGroupBuy.getShareAmount() + "개/g");
    }

    @Test
    void editJoinQuantityGroupBuyTest() {
        Member participantMember = memberRepository.findById(12).get();
        GroupBuy groupBuyNo = groupBuyRepository.findById(23).get();
        QuantityGroupBuy quantityGroupBuy = quantityGroupBuyRepository.findByGroupBuy(groupBuyNo);

        // 초기 결제 금액 계산: (참여 수량 * 단위 가격) * (1 + 수수료율)
        int participantQuantity = 1;
        Integer pricePerUnit = quantityGroupBuy.getPricePerUnit();
        Integer feeRate = groupBuyNo.getFeeRate();
        int initialPaymentPoint = (int) Math.round((participantQuantity * pricePerUnit * (1.0 + (feeRate / 100.0))));

        GroupBuyParticipant joinQuantityGroupBuy = groupBuyParticipantRepository.save(
                GroupBuyParticipant.builder()
                        .participant(participantMember)
                        .groupBuy(groupBuyNo)
                        .participatedDate(LocalDateTime.now())
                        .myQuantity(participantQuantity)
                        .initialPaymentPoint(initialPaymentPoint)
                        .build()
        );

        System.out.println("참여 신청 수량: " + joinQuantityGroupBuy.getMyQuantity() + "개/g");
        System.out.println("계산된 초기 결제 금액: " + joinQuantityGroupBuy.getInitialPaymentPoint() + "원");
    }

    @Test
    void editPayQuantityPointTest() {
        Member participantMember = memberRepository.findById(12).get();
        GroupBuyParticipant groupBuyParticipant = groupBuyParticipantRepository.findById(64).get();

        int initialPaymentPoint = groupBuyParticipant.getInitialPaymentPoint();
        int currentPoint = participantMember.getPoint();
        int newPoint = currentPoint - initialPaymentPoint;

        if (newPoint < 0) {
            System.out.println("잔액 부족");
            return;
        }

        participantMember.setPoint(newPoint);
        Member payQuantityMember = memberRepository.save(participantMember);

        System.out.println("초기 결제 금액: " + initialPaymentPoint + "P, 이전 포인트: " + currentPoint + "원, 새 포인트: " + newPoint + "원");
    }

    @Test
    void editStatusToClosedQuantityMetTest() {
        Integer GroupBuyId = 23;
        GroupBuy groupBuy = groupBuyRepository.findById(GroupBuyId).get();

        QuantityGroupBuy quantityGroupBuy = quantityGroupBuyRepository.findByGroupBuy(groupBuy);

        int initialMyQuantity = quantityGroupBuy.getMyQuantity();
        int currentSharedQuantity = 3;
        int totalQuantity = groupBuy.getQuantity();

        // A. 수량 충족 확인 및 상태 업데이트
        boolean isQuantitySatisfied = (currentSharedQuantity + initialMyQuantity >= totalQuantity);

        groupBuy.setStatus("closed");
        GroupBuy updatedGroupBuy = groupBuyRepository.save(groupBuy);
        QuantityGroupBuy finalQuantityGroupBuy = quantityGroupBuyRepository.save(quantityGroupBuy); // QuantityGroupBuy는 변경 없으므로 저장해도 무방

        System.out.println("[테스트 1. 수량 충족] GroupBuy ID " + GroupBuyId + "의 상태가 " + updatedGroupBuy.getStatus() + "로 업데이트되었습니다.");
        System.out.println("최종 개설자 부담 수량: " + finalQuantityGroupBuy.getMyQuantity() + "g (변동 없음)");
    }

    @Test
    void editStatusToClosedCreatorForcedCloseTest() {
        Integer GroupBuyId = 23;
        GroupBuy groupBuy = groupBuyRepository.findById(GroupBuyId).get();

        QuantityGroupBuy quantityGroupBuy = quantityGroupBuyRepository.findByGroupBuy(groupBuy);

        int initialMyQuantity = quantityGroupBuy.getMyQuantity(); // 1
        int totalQuantity = groupBuy.getQuantity(); // 4
        // 수량 1만 채웠다고 가정
        int currentSharedQuantity = 1;

        boolean isQuantitySatisfied = (currentSharedQuantity + initialMyQuantity >= totalQuantity);
        int remainingQuantity = totalQuantity - (currentSharedQuantity + initialMyQuantity); // 4 - (1 + 1) = 2
        quantityGroupBuy.setMyQuantity(initialMyQuantity + remainingQuantity); // 1 + 2 = 3

        quantityGroupBuyRepository.save(quantityGroupBuy);
        groupBuy.setStatus("closed");
        GroupBuy updatedGroupBuy = groupBuyRepository.save(groupBuy);

        System.out.println("[테스트 2. 개설자 강제 진행] GroupBuy ID " + GroupBuyId + "의 상태가 " + updatedGroupBuy.getStatus() + "로 업데이트되었습니다.");
        System.out.println("최종 개설자 부담 수량: " + quantityGroupBuy.getMyQuantity() + "g (잔여 수량 " + remainingQuantity + " 추가 부담)");
    }

    @Test
    void editCancelJoinGroupBuyTest() {
        Member participantMember = memberRepository.findById(12).get();
        GroupBuyParticipant groupBuyParticipant = groupBuyParticipantRepository.findById(64).get();

        groupBuyParticipant.setCancelDate(LocalDateTime.now());
        groupBuyParticipantRepository.save(groupBuyParticipant);

        int initialPaymentPoint = groupBuyParticipant.getInitialPaymentPoint(); // 지불한 돈 (환불액)
        int currentPoint = participantMember.getPoint(); // Mock current point
        int newPoint = currentPoint + initialPaymentPoint; // 현재 포인트 + 지불했던 포인트 (전액 환불)

        participantMember.setPoint(newPoint);
        memberRepository.save(participantMember);

        System.out.println("참여자 취소로 전액 환불: " + initialPaymentPoint + "P, 새 포인트: " + newPoint + "P");
    }

    @Test
        // [개설자 중단] GroupBuy 상태 CANCELED로 변경 및 참여자 전액 환불")
    void editQuantityCreatorCancelAndRefundTest() {
        Integer targetGroupBuyId = 23;
        String cancelStatus = "canceled";
        String cancelReason = "양배추 가격이 올랐습니다"; // 취소 사유

        GroupBuy groupBuy = groupBuyRepository.findById(targetGroupBuyId).get();

        Integer memberNo = 12;
        Integer participantId = 70;
        Member participantMember = memberRepository.findById(memberNo).get();
        GroupBuyParticipant participantEntry = groupBuyParticipantRepository.findById(participantId).get();

        int initialPaymentPoint = participantEntry.getInitialPaymentPoint();
        int initialMemberPoint = participantMember.getPoint();

        groupBuy.setStatus(cancelStatus);
        groupBuy.setCancelReason(cancelReason);
        GroupBuy updatedGroupBuy = groupBuyRepository.save(groupBuy);

        // 포인트 환불 로직
        int refundAmount = initialPaymentPoint; // 전액 환불

        Integer currentPoint = participantMember.getPoint();
        participantMember.setPoint(currentPoint + refundAmount);
        memberRepository.save(participantMember);

        participantEntry.setCancelDate(LocalDateTime.now());
        GroupBuyParticipant updatedParticipantEntry = groupBuyParticipantRepository.save(participantEntry);

        System.out.println("수량공구 GroupBuy ID " + targetGroupBuyId + "가 중단되고, 참여자 ID " + memberNo + "에게 " + refundAmount + "원 환불되었습니다.");
    }


}