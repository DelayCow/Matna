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
public class PeriodGroupBuyRepositoryTests {
    @Autowired
    IngredientRepository ingredientRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    GroupBuyRepository groupBuyRepository;
    @Autowired
    PeriodGroupBuyRepository periodGroupBuyRepository;
    @Autowired
    GroupBuyParticipantRepository groupBuyParticipantRepository;

    @Test
    void getSearchIngredientKeywordTest(){
        String keyword = "쌀";
        List<Ingredient> results = ingredientRepository.findByIngredientNameContaining(keyword);
        for (Ingredient ingredient : results) {
            System.out.println(ingredient.getIngredientName());
        }
    }

    @Test
    void addIngredientTest(){
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
    void addPeriodGroupBuyTest(){
        Ingredient ingredientNo = ingredientRepository.findById(23).get();
        Member creatorMember = memberRepository.findById(16).get();
        GroupBuy newGroupBuy = groupBuyRepository.save(
                GroupBuy.builder()
                .ingredient(ingredientNo) //밤고구마
                .creator(creatorMember)
                .title("김치/동치미랑 먹으면 딱좋은 밤고구마 20kg 같이 사요")
                .buyEndDate(3)
                .shareEndDate(4)
                .shareTime(LocalTime.parse("17:00")) //시간 데이터만
                .shareLocation("서울 금천구 가산디지털1로 70")
                .shareDetailAddress("호서대벤처 1층입구")
                .price(65000)
                .quantity(20000)
                .unit("g")
                .feeRate(3)
                .imageUrl("http://example.com/image_path/goguma.jpg")
                .content("유기농 밤고구마예요~ 저렴한데 양이 너무 많아요")
                .inDate(LocalDateTime.now())
                .itemSaleUrl("http://sale.site/item/123")
                .scrapCount(0)
                .status("open")
                .build()
        );
        PeriodGroupBuy newPeriodGroupBuy = periodGroupBuyRepository.save(
                PeriodGroupBuy.builder()
                .groupBuy(newGroupBuy) // 부모 GroupBuy 엔티티 연결 (FK 설정)
                .dueDate(LocalDateTime.of(2025,11,30,17,30))
                .maxParticipants(10)
                .build()
        );
        System.out.println(newGroupBuy.getTitle());
        System.out.println(newPeriodGroupBuy.getDueDate());
    }

    @Test
    void editJoinPeriodGroupBuyTest(){
        Member participantMember = memberRepository.findById(12).get();
        GroupBuy groupBuyNo = groupBuyRepository.findById(25).get();

        Integer price = groupBuyNo.getPrice();
        Integer feeRate = groupBuyNo.getFeeRate();
        int initialPaymentPoint = (int) Math.round((price * (1.0 + (feeRate / 100.0))) / 2.0);

        GroupBuyParticipant joinPeriodGroupBuy = groupBuyParticipantRepository.save(
                GroupBuyParticipant.builder()
                        .participant(participantMember)
                        .groupBuy(groupBuyNo)
                        .participatedDate(LocalDateTime.now())
                        .initialPaymentPoint(initialPaymentPoint)
                        .build()
        );
        System.out.println(joinPeriodGroupBuy.getInitialPaymentPoint());
    }

    @Test
    void editInitialPayPeriodPointTest(){
        Member participantMember = memberRepository.findById(12).get();
        GroupBuyParticipant groupBuyParticipant = groupBuyParticipantRepository.findById(66).get();

        int initialPaymentPoint = groupBuyParticipant.getInitialPaymentPoint(); // 차감할 포인트
        int currentPoint = participantMember.getPoint();
        int newPoint = currentPoint - initialPaymentPoint;
        if (newPoint < 0) {
            return; // 테스트 중단
        }

        participantMember.setPoint(newPoint);
        Member payPeroidMember = memberRepository.save(participantMember);

        System.out.println(initialPaymentPoint+" "+currentPoint+" "+newPoint);
    }



    @Test//  [마지막 참여/기간만료] GroupBuy 진행상황 (status) CLOSED 업데이트")
    void editStatusToClosedTest() {
        // Given: 상태를 변경할 GroupBuy (ID는 Long 타입으로 일관성 있게 변경)
        Integer GroupBuyId = 25;
        GroupBuy groupBuy = groupBuyRepository.findById(GroupBuyId).get();
        PeriodGroupBuy periodGroupBuy = periodGroupBuyRepository.findByGroupBuy(groupBuy);
        int currentParticipantCount = 5; // 현재 5명 참가했다고 가정
        int maxParticipants = periodGroupBuy.getMaxParticipants(); // 10명이라고 가정

        groupBuy.setStatus("closed");
        GroupBuy updatedGroupBuy = groupBuyRepository.save(groupBuy);

        assertEquals("closed", updatedGroupBuy.getStatus(), "GroupBuy의 상태는 'closed'여야 합니다.");
        System.out.println("[테스트 6] GroupBuy ID " + GroupBuyId + "의 상태가 " + updatedGroupBuy.getStatus() + "로 업데이트되었습니다. (Max: " + maxParticipants + ")");
        System.out.println(updatedGroupBuy.toString());
    }

    @Test
    // [마지막 참여/기간만료] 참여자 최종 수량 및 최종 결제금액 확정 업데이트"
    void editUpdateFinalAmountsAndQuantityTest() {
        Integer targetParticipantId = 66; // testJoinPeriodGroupBuy에서 사용된 ID를 가정
        GroupBuyParticipant participant = groupBuyParticipantRepository.findById(targetParticipantId)
                .orElseThrow(() -> new AssertionError("테스트를 위한 Participant 엔티티(ID: " + targetParticipantId + ")를 찾을 수 없습니다."));

        // 확정될 최종 데이터
        int finalPaymentPoint = 8000; //추후 계산된값 사용
        int finalQuantity = 5000; //추후 계산된값 사용

        participant.setFinalPaymentPoint(finalPaymentPoint);
        participant.setMyQuantity(finalQuantity);
        GroupBuyParticipant updatedParticipant = groupBuyParticipantRepository.save(participant);

        assertEquals(finalPaymentPoint, updatedParticipant.getFinalPaymentPoint(), "FinalPaymentPoint는 " + finalPaymentPoint + "여야 합니다.");
        assertEquals(finalQuantity, updatedParticipant.getMyQuantity(), "FinalQuantity는 " + finalQuantity + "여야 합니다.");
        System.out.println("[테스트 7] Participant ID " + targetParticipantId + "의 최종 데이터가 확정되었습니다: " + updatedParticipant.toString());
    }

    @Test
    // [마지막 참여/기간만료] 최종 금액 차액 환불 (Member Point 업데이트)")
    void editRefundDifferencePointsTest() {
        Integer targetParticipantId = 66;
        GroupBuyParticipant participant = groupBuyParticipantRepository.findById(targetParticipantId)
                .orElseThrow(() -> new AssertionError("테스트를 위한 Participant 엔티티(ID: " + targetParticipantId + ")를 찾을 수 없습니다."));

        // 환불 로직에 필요한 데이터 설정 (테스트를 위해 데이터를 임시로 설정)
        // GroupBuyParticipant 엔티티에 finalPaymentPoint 필드가 존재한다고 가정하고 사용합니다.
        int initialPaymentPoint = participant.getInitialPaymentPoint() > 0 ? participant.getInitialPaymentPoint() : 10000;
        int finalPaymentPoint = 8000; // 최종 금액이 더 낮다고 가정 (차액 발생)
        int refundAmount = initialPaymentPoint - finalPaymentPoint;
        participant.setFinalPaymentPoint(finalPaymentPoint);

        Member member = participant.getParticipant();
        int currentPoint = member.getPoint();

        int newPoint = currentPoint + refundAmount;
        member.setPoint(newPoint);
        Member updatedMember = memberRepository.save(member);

        // Then: 포인트가 정확히 환불 금액만큼 증가했는지 확인
        assertEquals(currentPoint + refundAmount, updatedMember.getPoint(),
                "Member의 포인트는 환불액(" + refundAmount + ")만큼 증가해야 합니다.");
        System.out.println("[테스트 8] Member ID " + updatedMember.getMemberNo() + "에게 " + refundAmount + "P가 환불되었습니다. New Point: " + updatedMember.getPoint());
        System.out.println(updatedMember.toString());
    }

    @Test
    void editCancelJoinGroupBuyTest(){
        Member participantMember = memberRepository.findById(12).get();
        GroupBuyParticipant groupBuyParticipant = groupBuyParticipantRepository.findById(66).get();

        groupBuyParticipant.setCancelDate(LocalDateTime.now());
        groupBuyParticipantRepository.save(groupBuyParticipant);

        int initialPaymentPoint = groupBuyParticipant.getInitialPaymentPoint(); //지불한돈
        int currentPoint = participantMember.getPoint();
        int newPoint = currentPoint + initialPaymentPoint; // 현재 포인트 + 지불했던 포인트
        participantMember.setPoint(newPoint);
        memberRepository.save(participantMember);

        System.out.println(initialPaymentPoint+" "+currentPoint+" "+newPoint);
    }

    @Test
    // [개설자 중단] GroupBuy 상태 CANCELED로 변경 및 참여자 전액 환불")
    void editPeriodCreatorCancelAndRefundTest() {
        Integer targetGroupBuyId = 25;
        String cancelStatus = "canceled";
        String cancelReason = "고구마철이 끝났다고 합니다.";

        GroupBuy groupBuy = groupBuyRepository.findById(targetGroupBuyId).get();

        Integer memberNo = 12;
        Integer participantId = 68;

        // 참여자 및 초기 포인트 조회 (환불 전 상태)
        Member participantMember = memberRepository.findById(memberNo).get();
        GroupBuyParticipant participantEntry = groupBuyParticipantRepository.findById(participantId).get();

        int initialPaymentPoint = participantEntry.getInitialPaymentPoint();
        int initialMemberPoint = participantMember.getPoint();

        groupBuy.setStatus(cancelStatus);
        groupBuy.setCancelReason(cancelReason);
        GroupBuy updatedGroupBuy = groupBuyRepository.save(groupBuy);

        Integer currentPoint = participantMember.getPoint();
        participantMember.setPoint(currentPoint + initialPaymentPoint);
        memberRepository.save(participantMember);

        participantEntry.setCancelDate(LocalDateTime.now());
        GroupBuyParticipant updatedParticipantEntry = groupBuyParticipantRepository.save(participantEntry);

        Member refundedMember = memberRepository.findById(memberNo).get();
        int expectedPoint = initialMemberPoint + initialPaymentPoint;

        System.out.println("기간공구 GroupBuy ID " + targetGroupBuyId + "가 중단되고, 참여자 ID " + memberNo + "에게 " + initialPaymentPoint + "원 환불되었습니다.");
        System.out.println("GroupBuy Status: " + updatedGroupBuy.getStatus() + ", Cancel Reason: " + updatedGroupBuy.getCancelReason());
        System.out.println("Member New Point: " + refundedMember.getPoint());
    }

    @Test
    public void editPointTest(){
        Integer memberNo = 5;
        Integer updatePoint = 100;
        Member m = memberRepository.findById(memberNo).get();
        if(m.getPoint() + updatePoint < 0){
            throw new IllegalArgumentException("최대 환급금액은 " + m.getPoint() + "원입니다.");
        }
        m.setPoint(m.getPoint() + updatePoint);
        memberRepository.save(m);
    }
}
