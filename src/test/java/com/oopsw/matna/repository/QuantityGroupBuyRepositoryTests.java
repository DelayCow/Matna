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
    QuantityGroupBuyRepository quantityGroupBuyRepository; // PeriodGroupBuyRepository -> QuantityGroupBuyRepository 변경
    @Autowired
    GroupBuyParticipantRepository groupBuyParticipantRepository;

//    @Test
//    void testSearchIngredientKeyword() {
//        String keyword = "쌀";
//        List<Ingredient> results = ingredientRepository.findByIngredientNameContaining(keyword);
//        for (Ingredient ingredient : results) {
//            System.out.println(ingredient.getIngredientName());
//        }
//    }
//
//    @Test
//    void testAddIngredient() {
//        Member creatorMember = memberRepository.findById(5).get();
//        Ingredient newIngredient = ingredientRepository.save(
//                Ingredient.builder()
//                        .ingredientName("모닝빵")
//                        .creator(creatorMember)
//                        .inDate(LocalDateTime.now())
//                        .build());
//        System.out.println(newIngredient.getIngredientName());
//    }

    @Test
    void testAddQuantityGroupBuy() { // testAddPeriodGroupBuy -> testAddQuantityGroupBuy 변경
        Ingredient ingredientNo = ingredientRepository.findById(45).get();
        Member creatorMember = memberRepository.findById(16).get();

        // 1. GroupBuy 기본 정보 저장 (수량 관련 필드 포함)
        GroupBuy newGroupBuy = groupBuyRepository.save(
                GroupBuy.builder()
                        .ingredientNo(ingredientNo) //양배추
                        .creatorNo(creatorMember)
                        .title("양배추 4통 같이 나눌분")
                        .buyEndDate(3)
                        .shareEndDate(2)
                        .shareTime(LocalTime.parse("18:00")) //시간 데이터만
                        .shareLocation("서울 금천구 가산디지털1로 70")
                        .shareDetailAddress("호서대벤처 1층입구")
                        .price(12000) // 총 가격
                        .quantity(4) // 총 수량 (개)
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

        // 2. QuantityGroupBuy 전용 정보 저장
        QuantityGroupBuy newQuantityGroupBuy = quantityGroupBuyRepository.save(
                QuantityGroupBuy.builder()
                        .groupBuyNo(newGroupBuy) // 부모 GroupBuy 엔티티 연결 (FK 설정)
                        .myQuantity(1) // 개설자 부담 수량
                        .shareAmount(1) // 나눔 단위 수량
                        .pricePerUnit(3000) // 계산해야되는값
                        .build()
        );

        System.out.println(newGroupBuy.getTitle());
        System.out.println("개설자 부담 수량: " + newQuantityGroupBuy.getMyQuantity() + "개/g");
        System.out.println("나눔 단위: " + newQuantityGroupBuy.getShareAmount() + "개/g");
    }

    @Test
    void testJoinQuantityGroupBuy() { // testJoinPeriodGroupBuy -> testJoinQuantityGroupBuy 변경
        Member participantMember = memberRepository.findById(12).get();
        GroupBuy groupBuyNo = groupBuyRepository.findById(32).get();

        // GroupBuy 엔티티에서 QuantityGroupBuy의 pricePerUnit, shareAmount 정보를 조회해야 하나,
        // 테스트의 단순화를 위해 QuantityGroupBuy 엔티티를 직접 조회하거나 필요한 값을 하드코딩합니다.
        // 여기서는 QuantityGroupBuy 엔티티를 조회하여 필요한 정보를 가져옵니다.
        QuantityGroupBuy quantityGroupBuy = quantityGroupBuyRepository.findByGroupBuyNo(groupBuyNo);

        // [수량 공구 로직] 참여 수량(Quantity) 기반 결제 금액 계산
        int participantQuantity = 1; // 참여자가 3000g을 신청했다고 가정 (SHARE_AMOUNT의 배수)

        // QuantityGroupBuy의 단위 가격과 GroupBuy의 수수료율 사용
        Integer pricePerUnit = quantityGroupBuy.getPricePerUnit(); // 3원/g
        Integer feeRate = groupBuyNo.getFeeRate(); // 3%

        // 초기 결제 금액 계산: (참여 수량 * 단위 가격) * (1 + 수수료율)
        int initialPaymentPoint = (int) Math.round((participantQuantity * pricePerUnit * (1.0 + (feeRate / 100.0))));

        GroupBuyParticipant joinQuantityGroupBuy = groupBuyParticipantRepository.save(
                GroupBuyParticipant.builder()
                        .participantNo(participantMember)
                        .groupBuyNo(groupBuyNo)
                        .participatedDate(LocalDateTime.now())
                        .myQuantity(participantQuantity) // 참여 신청 수량 기록
                        .initialPaymentPoint(initialPaymentPoint)
                        .build()
        );
        System.out.println("참여 신청 수량: " + joinQuantityGroupBuy.getMyQuantity() + "개/g");
        System.out.println("계산된 초기 결제 금액: " + joinQuantityGroupBuy.getInitialPaymentPoint() + "원");
    }

    @Test
    void testPayQuantityPoint() { // testInitialPayPeroidPoint -> testInitialPayQuantityPoint 변경
        Member participantMember = memberRepository.findById(12).get();
        GroupBuyParticipant groupBuyParticipant = groupBuyParticipantRepository.findById(70).get();

        int initialPaymentPoint = groupBuyParticipant.getInitialPaymentPoint(); // 차감할 포인트

        // 멤버 포인트는 null일 수 있으므로 0으로 초기화
        int currentPoint = participantMember.getPoint();
        int newPoint = currentPoint - initialPaymentPoint;

        if (newPoint < 0) {
            System.out.println("잔액 부족으로 테스트 중단: 초기 결제 금액 " + initialPaymentPoint + "원");
            return; // 테스트 중단
        }

        participantMember.setPoint(newPoint);
        Member payQuantityMember = memberRepository.save(participantMember);

        System.out.println("초기 결제 금액: " + initialPaymentPoint + "P, 이전 포인트: " + currentPoint + "원, 새 포인트: " + newPoint + "원");
    }

    @Test
        // [총 수량 충족/개설자 강제 진행] GroupBuy 진행상황 (status) CLOSED 업데이트")
    void testUpdateStatusToClosed() {
        // Given: 상태를 변경할 GroupBuy
        Integer GroupBuyId = 32;
        GroupBuy groupBuy = groupBuyRepository.findById(GroupBuyId)
                .orElseThrow(() -> new AssertionError("테스트를 위한 GroupBuy 엔티티(ID: " + GroupBuyId + ")를 찾을 수 없습니다."));

        // 1. QuantityGroupBuy 정보 조회
        QuantityGroupBuy quantityGroupBuy = quantityGroupBuyRepository.findByGroupBuyNo(groupBuy);

        // **수량 충족 조건 시뮬레이션**
        // 실제 서비스 로직에서는 DB에서 GroupBuyParticipant의 myQuantity 총합을 구해야 함

        int currentSharedQuantity = 3;
        int totalQuantity = groupBuy.getQuantity();
        int myQuantity = quantityGroupBuy.getMyQuantity();

        // 2. 수량 충족 검증 (currentSharedQuantity + myQuantity >= totalQuantity)
        // 또는 개설자 강제 마감 버튼 클릭 시
        boolean isQuantitySatisfied = (currentSharedQuantity + myQuantity >= totalQuantity);

        if (isQuantitySatisfied) {
            groupBuy.setStatus("closed");
        } else {
            // [개설자 강제 진행 시뮬레이션]
            // 수량 충족이 안 되었지만 개설자가 잔여 수량을 부담하고 강제 마감하는 로직을 시뮬레이션
            int remainingQuantity = totalQuantity - (currentSharedQuantity + myQuantity);
            if (remainingQuantity > 0) {
                // 개설자 부담 수량 업데이트 (잔여 수량 추가 부담)
                quantityGroupBuy.setMyQuantity(myQuantity + remainingQuantity);
                quantityGroupBuyRepository.save(quantityGroupBuy);
            }
            groupBuy.setStatus("closed");
        }

        GroupBuy updatedGroupBuy = groupBuyRepository.save(groupBuy);

        assertEquals("closed", updatedGroupBuy.getStatus(), "GroupBuy의 상태는 'closed'여야 합니다.");
        System.out.println("[테스트] GroupBuy ID " + GroupBuyId + "의 상태가 " + updatedGroupBuy.getStatus() + "로 업데이트되었습니다.");
        System.out.println("최종 개설자 부담 수량: " + quantityGroupBuy.getMyQuantity() + "g");
    }

    @Test
    void testCancelJoinGroupBuy() {
        Member participantMember = memberRepository.findById(12).get();
        GroupBuyParticipant groupBuyParticipant = groupBuyParticipantRepository.findById(70).get();

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
    void testQuantityCreatorCancelAndRefund() { // testPeriodCreatorCancelAndRefund -> testQuantityCreatorCancelAndRefund 변경
        Integer targetGroupBuyId = 32;
        String cancelStatus = "canceled";
        String cancelReason = "양배추 가격이 올랐습니다"; // 취소 사유

        GroupBuy groupBuy = groupBuyRepository.findById(targetGroupBuyId)
                .orElseThrow(() -> new AssertionError("테스트를 위한 GroupBuy 엔티티(ID: " + targetGroupBuyId + ")를 찾을 수 없습니다."));

        Integer memberNo = 12;
        Integer participantId = 70;

        // 참여자 및 초기 포인트 조회 (환불 전 상태)
        Member participantMember = memberRepository.findByMemberNo(memberNo);
        GroupBuyParticipant participantEntry = groupBuyParticipantRepository.findById(participantId)
                .orElseThrow(() -> new AssertionError("테스트 참여 엔티티를 찾을 수 없습니다. ID: " + participantId));

        int initialPaymentPoint = participantEntry.getInitialPaymentPoint() != null ? participantEntry.getInitialPaymentPoint() : 9270; // Mock initial payment
        int initialMemberPoint = participantMember.getPoint() != null ? participantMember.getPoint() : 100000; // Mock initial point

        // 3. GroupBuy 상태 및 취소 사유 업데이트 (개설자 중단)
        groupBuy.setStatus(cancelStatus);
        groupBuy.setCancelReason(cancelReason);
        GroupBuy updatedGroupBuy = groupBuyRepository.save(groupBuy);

        // 포인트 환불 로직
        int refundAmount = initialPaymentPoint; // 전액 환불

        Integer currentPoint = participantMember.getPoint();
        participantMember.setPoint(currentPoint + refundAmount);
        memberRepository.save(participantMember);

        // 참여 기록에 취소 날짜 기록
        participantEntry.setCancelDate(LocalDateTime.now());
        GroupBuyParticipant updatedParticipantEntry = groupBuyParticipantRepository.save(participantEntry);

        // A. GroupBuy 상태 검증
        assertEquals(cancelStatus, updatedGroupBuy.getStatus(), "GroupBuy의 상태는 'canceled'여야 합니다.");

        // B. Member 포인트 환불 검증
        Member refundedMember = memberRepository.findByMemberNo(memberNo);
        int expectedPoint = initialMemberPoint + refundAmount;
        assertEquals(expectedPoint, refundedMember.getPoint(), "참여자 포인트는 환불 금액만큼 증가해야 합니다.");

        // C. GroupBuyParticipant 취소일자 검증
        assertNotNull(updatedParticipantEntry.getCancelDate(), "참여 기록에 취소 날짜가 기록되어야 합니다.");

        System.out.println("수량공구 GroupBuy ID " + targetGroupBuyId + "가 중단되고, 참여자 ID " + memberNo + "에게 " + refundAmount + "P가 환불되었습니다.");
    }
}