package com.oopsw.matna.service;

import com.oopsw.matna.controller.groupbuy.PeriodRegisterRequest;
import com.oopsw.matna.repository.*;
import com.oopsw.matna.repository.entity.*;
import com.oopsw.matna.vo.GroupBuyParticipantVO;
import com.oopsw.matna.vo.PeriodGroupBuyDetailVO;
import com.oopsw.matna.vo.PeriodGroupBuyHomeVO;
import com.oopsw.matna.vo.PeroidGroupBuyCreateVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Transactional
public class PeriodGroupBuyServiceTests {
    @Autowired
    private PeriodGroupBuyService periodGroupBuyService;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PeriodGroupBuyRepository periodGroupBuyRepository;
    @Autowired
    private GroupBuyRepository groupBuyRepository;
    @Autowired
    private GroupBuyParticipantRepository groupBuyParticipantRepository;

    @Test
    void getIngredientKeywordTest() {
        String keyword = "쌀";
        List<Ingredient> results = periodGroupBuyService.getIngredientKeyword(keyword);
        results.forEach(ingredient -> {
            System.out.println(ingredient.getIngredientName());
        });
    }

    @Test
    void addIngredientTest() throws IOException {
        Integer creatorNo = 5;
        String ingredientName = "브로콜리";
        // 회원 존재 여부 확인
        memberRepository.findById(creatorNo)
                .orElseThrow(() -> new AssertionError("테스트용 회원(memberNo: " + creatorNo + ")이 존재하지 않습니다."));
        // 중복 재료명 확인
        List<Ingredient> existingIngredients = ingredientRepository.findByIngredientNameContaining(ingredientName);
        if (!existingIngredients.isEmpty()) {
            System.out.println("이미 존재하는 재료명입니다: " + ingredientName);
            return;
        }
        Ingredient savedIngredient = periodGroupBuyService.addIngredient(creatorNo, ingredientName);

        assertNotNull(savedIngredient);
        assertNotNull(savedIngredient.getIngredientNo());
        assertEquals(ingredientName, savedIngredient.getIngredientName());
        assertEquals(creatorNo, savedIngredient.getCreator().getMemberNo());
        assertNotNull(savedIngredient.getInDate());

        System.out.println("저장된 재료: " + savedIngredient.getIngredientName());
        System.out.println("생성자 번호: " + savedIngredient.getCreator().getMemberNo());
    }

    @Test
    void addPeriodGroupBuyTest() throws IOException {
        Integer ingredientNo = 23;
        Integer creatorNo = 16;

        // 재료 존재 여부 확인
        ingredientRepository.findById(ingredientNo)
                .orElseThrow(() -> new AssertionError("테스트용 재료(ingredientNo: " + ingredientNo + ")가 존재하지 않습니다."));

        // 회원 존재 여부 확인
        memberRepository.findById(creatorNo)
                .orElseThrow(() -> new AssertionError("테스트용 회원(memberNo: " + creatorNo + ")이 존재하지 않습니다."));

        // Request 객체 생성
        PeriodRegisterRequest request = PeriodRegisterRequest.builder()
                .ingredientNo(ingredientNo)
                .creatorNo(creatorNo)
                .title("김치/동치미랑 먹으면 딱좋은 밤고구마 20kg 같이 사요")
                .buyEndDate(3)
                .shareEndDate(4)
                .shareTime(LocalTime.parse("17:00"))
                .shareLocation("서울 금천구 가산디지털1로 70")
                .shareDetailAddress("호서대벤처 1층입구")
                .price(65000)
                .quantity(20000)
                .unit("g")
                .feeRate(3)
                .content("유기농 밤고구마예요~ 저렴한데 양이 너무 많아요")
                .itemSaleUrl("http://sale.site/item/123")
                .dueDate(LocalDateTime.of(2025, 12, 30, 17, 30))
                .maxParticipants(10)
                .build();

        // Mock 이미지 파일 생성
        MockMultipartFile mockFile = new MockMultipartFile(
                "thumbnailFile",
                "goguma.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // Service 메소드 호출
        PeriodGroupBuy result = periodGroupBuyService.addPeriodGroupBuy(request, mockFile);

        // 검증
        assertNotNull(result);
        assertNotNull(result.getPeriodGroupBuyNo());
        assertNotNull(result.getGroupBuy());

        // GroupBuy 검증
        GroupBuy groupBuy = result.getGroupBuy();
        assertEquals("김치/동치미랑 먹으면 딱좋은 밤고구마 20kg 같이 사요", groupBuy.getTitle());
        assertEquals(ingredientNo, groupBuy.getIngredient().getIngredientNo());
        assertEquals(creatorNo, groupBuy.getCreator().getMemberNo());
        assertEquals(65000, groupBuy.getPrice());
        assertEquals(20000, groupBuy.getQuantity());
        assertEquals("g", groupBuy.getUnit());
        assertEquals(3, groupBuy.getFeeRate());
        assertEquals("open", groupBuy.getStatus());
        assertEquals(0, groupBuy.getScrapCount());
        assertEquals("서울 금천구 가산디지털1로 70", groupBuy.getShareLocation());
        assertEquals("호서대벤처 1층입구", groupBuy.getShareDetailAddress());
        assertNotNull(groupBuy.getImageUrl()); // 이미지 URL이 저장되었는지 확인

        // PeriodGroupBuy 검증
        assertEquals(LocalDateTime.of(2025, 12, 30, 17, 30), result.getDueDate());
        assertEquals(10, result.getMaxParticipants());

        // DB 조회 확인
        PeriodGroupBuy savedPeriodGroupBuy = periodGroupBuyRepository.findById(result.getPeriodGroupBuyNo())
                .orElseThrow(() -> new AssertionError("저장된 기간 공동구매를 찾을 수 없습니다."));

        assertEquals(result.getPeriodGroupBuyNo(), savedPeriodGroupBuy.getPeriodGroupBuyNo());

        System.out.println("GroupBuy 제목: " + groupBuy.getTitle());
        System.out.println("이미지 URL: " + groupBuy.getImageUrl());
        System.out.println("PeriodGroupBuy 마감일: " + result.getDueDate());
        System.out.println("최대 참여자 수: " + result.getMaxParticipants());
    }

    @Test
    void addParticipantToPeriodGroupBuyTest() {
        Integer participantNo = 12;
        Integer groupBuyNo = 29;
        // 참여자 존재 여부 확인
        Member member = memberRepository.findById(participantNo)
                .orElseThrow(() -> new AssertionError("테스트용 회원(memberNo: " + participantNo + ")이 존재하지 않습니다."));
        // 공동구매 존재 여부 확인
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyNo)
                .orElseThrow(() -> new AssertionError("테스트용 공동구매(groupBuyNo: " + groupBuyNo + ")가 존재하지 않습니다."));


        Integer beforePoint = member.getPoint();
        Integer price = groupBuy.getPrice();
        Integer feeRate = groupBuy.getFeeRate();
        int expectedInitialPaymentPoint = (int) Math.round((price * (1.0 + (feeRate / 100.0))) / 2.0);

        GroupBuyParticipantVO vo = GroupBuyParticipantVO.builder()
                .participantNo(participantNo)
                .groupBuyNo(groupBuyNo)
                .build();

        if (beforePoint >= expectedInitialPaymentPoint) {
            // 정상 케이스
            GroupBuyParticipant result = periodGroupBuyService.addParticipantToPeriodGroupBuy(vo);

            assertNotNull(result);
            assertNotNull(result.getGroupParticipantNo());
            assertEquals(participantNo, result.getParticipant().getMemberNo());
            assertEquals(groupBuyNo, result.getGroupBuy().getGroupBuyNo());
            assertEquals(expectedInitialPaymentPoint, result.getInitialPaymentPoint());
            // 포인트 차감 확인
            Member updatedMember = memberRepository.findById(participantNo).get();
            assertEquals(beforePoint - expectedInitialPaymentPoint, updatedMember.getPoint());

            System.out.println("참여 전 포인트: " + beforePoint);
            System.out.println("참여 후 포인트: " + updatedMember.getPoint());
            System.out.println("차감된 포인트: " + expectedInitialPaymentPoint);

        } else {
            // 예외 케이스: 포인트 부족
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> periodGroupBuyService.addParticipantToPeriodGroupBuy(vo)
            );

            assertTrue(exception.getMessage().contains("포인트가 부족합니다"));
            System.out.println("현재 포인트: " + beforePoint);
            System.out.println("필요 포인트: " + expectedInitialPaymentPoint);
            System.out.println("예외 메시지: " + exception.getMessage());
        }
    }

    @Test
    void editGroupBuyStatusAndRefundTest() {
        Integer groupBuyNo = 29;
        // 공동구매 존재 여부 확인
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyNo)
                .orElseThrow(() -> new AssertionError("groupBuyNo: " + groupBuyNo + "가 존재하지 않습니다."));
        // PeriodGroupBuy 조회
        PeriodGroupBuy periodGroupBuy = periodGroupBuyRepository.findByGroupBuy(groupBuy);
        assertNotNull(periodGroupBuy, "PeriodGroupBuy가 존재해야 합니다.");
        // 참여자 목록 조회
        List<GroupBuyParticipant> participants = groupBuyParticipantRepository.findByGroupBuy(groupBuy);
        assertFalse(participants.isEmpty(), "참여자가 최소 1명 이상 존재해야 합니다.");

        int participantCount = participants.size();
        int maxParticipants = periodGroupBuy.getMaxParticipants();
        LocalDateTime dueDate = periodGroupBuy.getDueDate();

        // 마감 조건 확인
        boolean isMaxParticipantsReached = participantCount >= (maxParticipants - 1);
        boolean isDueDatePassed = LocalDateTime.now().isAfter(dueDate);
        boolean canFinalize = isMaxParticipantsReached || isDueDatePassed;

        if (canFinalize) {
            // 정상 케이스: 마감 조건 충족
            System.out.println("현재 참여자: " + participantCount + ", 필요 참여자: " + (maxParticipants - 1));
            System.out.println("현재 시간: " + LocalDateTime.now() + ", 마감 시간: " + dueDate);
            System.out.println("최대 참여자 도달: " + isMaxParticipantsReached);
            System.out.println("기간 만료: " + isDueDatePassed);

            // 참여자들의 초기 포인트 저장
            Map<Integer, Integer> beforePoints = new HashMap<>();
            for (GroupBuyParticipant participant : participants) {
                Member member = participant.getParticipant();
                beforePoints.put(member.getMemberNo(), member.getPoint());
            }

            // 예상 최종 금액 계산
            Integer totalPrice = groupBuy.getPrice();
            Integer totalQuantity = groupBuy.getQuantity();
            Integer feeRate = groupBuy.getFeeRate();

            int expectedFinalQuantity = totalQuantity / participantCount;
            int expectedFinalPaymentPoint = (int) Math.round(
                    (totalPrice * (1.0 + (feeRate / 100.0))) / participantCount
            );

            periodGroupBuyService.editGroupBuyStatusAndRefund(groupBuyNo);

            // GroupBuy 상태 확인
            GroupBuy updatedGroupBuy = groupBuyRepository.findById(groupBuyNo).get();
            assertEquals("closed", updatedGroupBuy.getStatus(), "GroupBuy 상태가 'closed'여야 합니다.");
            System.out.println("\nGroupBuy 상태: " + updatedGroupBuy.getStatus());

            // 각 참여자의 최종 금액 및 수량 확인
            List<GroupBuyParticipant> updatedParticipants = groupBuyParticipantRepository.findByGroupBuy(groupBuy);
            for (GroupBuyParticipant participant : updatedParticipants) {
                // 최종 수량 확인
                assertEquals(expectedFinalQuantity, participant.getMyQuantity(),
                        "참여자의 최종 수량이 " + expectedFinalQuantity + "이어야 합니다.");
                // 최종 결제 금액 확인
                assertEquals(expectedFinalPaymentPoint, participant.getFinalPaymentPoint(),
                        "참여자의 최종 결제 금액이 " + expectedFinalPaymentPoint + "이어야 합니다.");
                // 환불 금액 확인
                int initialPaymentPoint = participant.getInitialPaymentPoint();
                int expectedRefund = initialPaymentPoint - expectedFinalPaymentPoint;

                Member member = memberRepository.findById(participant.getParticipant().getMemberNo()).get();
                Integer beforePoint = beforePoints.get(member.getMemberNo());
                Integer expectedAfterPoint = beforePoint + expectedRefund;

                assertEquals(expectedAfterPoint, member.getPoint(),
                        "회원의 포인트가 환불 금액만큼 증가해야 합니다.");

                System.out.println("초기 결제 포인트: " + initialPaymentPoint);
                System.out.println("최종 결제 포인트: " + participant.getFinalPaymentPoint());
                System.out.println("최종 수량: " + participant.getMyQuantity() + "g");
                System.out.println("환불 금액: " + expectedRefund);
                System.out.println("참여 전 포인트: " + beforePoint);
                System.out.println("참여 후 포인트: " + member.getPoint());
            }

            System.out.println("총 참여자 수: " + participantCount);
            System.out.println("1인당 최종 수량: " + expectedFinalQuantity + "g");
            System.out.println("1인당 최종 금액: " + expectedFinalPaymentPoint + "원");

        } else {
            // 예외 케이스: 마감 조건 미충족
            System.out.println("=== 예외 케이스: 마감 조건 미충족 ===");
            System.out.println("현재 참여자: " + participantCount + ", 필요 참여자: " + (maxParticipants - 1));
            System.out.println("현재 시간: " + LocalDateTime.now() + ", 마감 시간: " + dueDate);
            System.out.println("최대 참여자 도달: " + isMaxParticipantsReached);
            System.out.println("기간 만료: " + isDueDatePassed);

            // when & then
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> periodGroupBuyService.editGroupBuyStatusAndRefund(groupBuyNo)
            );
            assertTrue(exception.getMessage().contains("공동구매를 마감할 수 없습니다"),
                    "마감 조건 미충족 예외 메시지가 포함되어야 합니다.");
            System.out.println("\n예외 발생 확인: " + exception.getMessage());
        }
    }

    @Test
    void editCancelParticipantGroupBuyTest() {
        Integer participantNo = 12;
        Integer groupBuyParticipantNo = 67;
        // 참여자 존재 여부 확인
        Member member = memberRepository.findById(participantNo)
                .orElseThrow(() -> new AssertionError("테스트용 회원(memberNo: " + participantNo + ")이 존재하지 않습니다."));
        // 참여 정보 존재 여부 확인
        GroupBuyParticipant groupBuyParticipant = groupBuyParticipantRepository.findById(groupBuyParticipantNo)
                .orElseThrow(() -> new AssertionError("테스트용 참여 정보(participantNo: " + groupBuyParticipantNo + ")가 존재하지 않습니다."));
        // 이미 취소된 참여인지 확인
        if (groupBuyParticipant.getCancelDate() != null) {
            System.out.println("이미 취소된 참여입니다. 취소일: " + groupBuyParticipant.getCancelDate());
            return;
        }
        // 공동구매 상태 확인
        GroupBuy groupBuy = groupBuyParticipant.getGroupBuy();
        boolean isClosed = "closed".equals(groupBuy.getStatus());

        if (isClosed) {
            //예외 케이스: 마감된 공동구매
            System.out.println("공동구매 상태: " + groupBuy.getStatus());

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> periodGroupBuyService.editCancelParticipantGroupBuy(groupBuyParticipantNo)
            );
            assertTrue(exception.getMessage().contains("마감된 공동구매는 취소할 수 없습니다"),
                    "마감된 공동구매 예외 메시지가 포함되어야 합니다.");
            System.out.println("예외 발생 확인: " + exception.getMessage());

        } else {
            // 정상 케이스: 참여 취소 및 환불
            System.out.println("공동구매 상태: " + groupBuy.getStatus());
            // 취소 전 데이터
            int beforePoint = member.getPoint();
            int initialPaymentPoint = groupBuyParticipant.getInitialPaymentPoint();
            int expectedAfterPoint = beforePoint + initialPaymentPoint;

            periodGroupBuyService.editCancelParticipantGroupBuy(groupBuyParticipantNo);
            // 취소 일시 확인
            GroupBuyParticipant updatedParticipant = groupBuyParticipantRepository.findById(groupBuyParticipantNo).get();
            assertNotNull(updatedParticipant.getCancelDate(), "취소 일시가 설정되어야 합니다.");
            // 포인트 환불 확인
            Member updatedMember = memberRepository.findById(participantNo).get();
            assertEquals(expectedAfterPoint, updatedMember.getPoint(),
                    "회원의 포인트가 초기 결제 금액만큼 환불되어야 합니다.");

            System.out.println("참여자 ID: " + groupBuyParticipantNo);
            System.out.println("초기 결제 포인트: " + initialPaymentPoint);
            System.out.println("취소 전 포인트: " + beforePoint);
            System.out.println("취소 후 포인트: " + updatedMember.getPoint());
            System.out.println("환불 금액: " + initialPaymentPoint);
            System.out.println("취소 일시: " + updatedParticipant.getCancelDate());
        }
    }

    @Test
    void editPeriodCreatorCancelAndRefundTest() {
        Integer groupBuyNo = 29;
        String cancelReason = "고구마철이 끝났다고 합니다.";
        // 공동구매 존재 여부 확인
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyNo)
                .orElseThrow(() -> new AssertionError("groupBuyNo: " + groupBuyNo + "가 존재하지 않습니다."));
        // 이미 취소된 경우 테스트 스킵
        if ("canceled".equals(groupBuy.getStatus())) {
            System.out.println("이미 취소된 공동구매입니다. 상태: " + groupBuy.getStatus());
            return;
        }
        // 공동구매 상태 확인
        boolean isClosed = "closed".equals(groupBuy.getStatus());

        if (isClosed) {
            //예외 케이스: 이미 마감된 공동구매
            System.out.println("공동구매 ID: " + groupBuyNo);
            System.out.println("공동구매 상태: " + groupBuy.getStatus());

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> periodGroupBuyService.editPeriodCreatorCancelAndRefund(groupBuyNo, cancelReason)
            );
            assertTrue(exception.getMessage().contains("이미 마감된 공동구매는 취소할 수 없습니다"),
                    "마감된 공동구매 예외 메시지가 포함되어야 합니다.");
            System.out.println("예외 발생 확인: " + exception.getMessage());

        } else {
            // 정상 케이스: 공동구매 중단 및 환불
            System.out.println("공동구매 ID: " + groupBuyNo);
            System.out.println("공동구매 상태: " + groupBuy.getStatus());
            // 참여자 목록 조회
            List<GroupBuyParticipant> participants = groupBuyParticipantRepository.findByGroupBuy(groupBuy);

            if (participants.isEmpty()) {
                System.out.println("참여자가 없어 환불 테스트를 진행할 수 없습니다.");
                return;
            }
            // 취소 전 각 참여자의 포인트 저장
            Map<Integer, Integer> beforePoints = new HashMap<>();
            Map<Integer, Integer> refundAmounts = new HashMap<>();

            for (GroupBuyParticipant participant : participants) {
                if (participant.getCancelDate() != null) {
                    continue;
                }
                Member member = participant.getParticipant();
                beforePoints.put(member.getMemberNo(), member.getPoint());
                refundAmounts.put(member.getMemberNo(), participant.getInitialPaymentPoint());
            }
            System.out.println("참여자 수: " + participants.size());
            System.out.println("환불 대상 참여자 수: " + beforePoints.size());


            periodGroupBuyService.editPeriodCreatorCancelAndRefund(groupBuyNo, cancelReason);
            // 1. GroupBuy 상태 및 취소 사유 확인
            GroupBuy updatedGroupBuy = groupBuyRepository.findById(groupBuyNo).get();
            assertEquals("canceled", updatedGroupBuy.getStatus(), "GroupBuy 상태가 'canceled'여야 합니다.");
            assertEquals(cancelReason, updatedGroupBuy.getCancelReason(), "취소 사유가 저장되어야 합니다.");

            System.out.println("공동구매 상태: " + updatedGroupBuy.getStatus());
            System.out.println("취소 사유: " + updatedGroupBuy.getCancelReason());

            // 2. 각 참여자의 환불 확인
            List<GroupBuyParticipant> updatedParticipants = groupBuyParticipantRepository.findByGroupBuy(groupBuy);

            for (GroupBuyParticipant participant : updatedParticipants) {
                Member member = participant.getParticipant();
                Integer memberNo = member.getMemberNo();
                // 환불 대상이 아닌 참여자는 스킵
                if (!beforePoints.containsKey(memberNo)) {
                    continue;
                }
                // 취소 일시 확인
                assertNotNull(participant.getCancelDate(), "참여자의 취소 일시가 설정되어야 합니다.");
                // 포인트 환불 확인
                Member updatedMember = memberRepository.findById(memberNo).get();
                int beforePoint = beforePoints.get(memberNo);
                int refundAmount = refundAmounts.get(memberNo);
                int expectedPoint = beforePoint + refundAmount;

                assertEquals(expectedPoint, updatedMember.getPoint(),
                        "회원의 포인트가 초기 결제 금액만큼 환불되어야 합니다.");

                System.out.println("\n 참여자 ID: " + participant.getGroupParticipantNo());
                System.out.println("회원 번호: " + memberNo);
                System.out.println("환불 금액: " + refundAmount);
                System.out.println("환불 전 포인트: " + beforePoint);
                System.out.println("환불 후 포인트: " + updatedMember.getPoint());
                System.out.println("취소 일시: " + participant.getCancelDate());
            }
            System.out.println("총 환불 참여자 수: " + beforePoints.size());
        }
    }

    @Test
    void getPeriodGroupBuyHomeVariousConditionsTest() {
        // 1. 최신 등록순
        System.out.println("1. 최신 등록순");
        Map<String, Object> params1 = new HashMap<>();
        List<PeriodGroupBuyHomeVO> list1 = periodGroupBuyService.getPeriodGroupBuyHome(params1);
        assertNotNull(list1);
        System.out.println("조회 건수: " + list1.size());

        // 2. 마감 임박순
        System.out.println("2. 마감 임박순 ");
        Map<String, Object> params2 = new HashMap<>();
        params2.put("orderBy", "dueSoon");
        List<PeriodGroupBuyHomeVO> list2 = periodGroupBuyService.getPeriodGroupBuyHome(params2);
        assertNotNull(list2);
        System.out.println("조회 건수: " + list2.size());

        // 3. 키워드 검색 - "쌀"
        System.out.println("3. 키워드 검색: '쌀'");
        Map<String, Object> params3 = new HashMap<>();
        params3.put("keyword", "쌀");
        List<PeriodGroupBuyHomeVO> list3 = periodGroupBuyService.getPeriodGroupBuyHome(params3);
        assertNotNull(list3);
        System.out.println("조회 건수: " + list3.size());

        // 4. 키워드 검색 + 마감 임박순
        System.out.println("4. 키워드 검색 + 마감 임박순: '고구마'");
        Map<String, Object> params4 = new HashMap<>();
        params4.put("keyword", "고구마");
        params4.put("orderBy", "deadline");
        List<PeriodGroupBuyHomeVO> list4 = periodGroupBuyService.getPeriodGroupBuyHome(params4);
        assertNotNull(list4);
        System.out.println("조회 건수: " + list4.size());
        if (!list4.isEmpty()) {
            System.out.println("첫 번째 결과: " + list4.get(0).getTitle());
        }

        // 5. null params
        System.out.println("5. null params (기본 조회)");
        List<PeriodGroupBuyHomeVO> list5 = periodGroupBuyService.getPeriodGroupBuyHome(null);
        assertNotNull(list5);
        System.out.println("조회 건수: " + list5.size());
    }

    @Test
    void getPeriodGroupBuyDetailTest() {
        Integer testPeriodGroupBuyNo = 14;

        Map<String, Object> response = periodGroupBuyService.getPeriodGroupBuyDetail(testPeriodGroupBuyNo);
        assertNotNull(response, "응답 객체가 null이 아니어야 합니다.");
        assertNotNull(response.get("groupBuyDetail"), "공동구매 상세 정보가 null이 아니어야 합니다.");
        assertNotNull(response.get("participants"), "참여자 목록이 null이 아니어야 합니다.");
        assertNotNull(response.get("recipes"), "레시피 목록이 null이 아니어야 합니다.");

        // 1. 공동구매 상세 정보 출력
        System.out.println(" 상세 정보");
        PeriodGroupBuyDetailVO detailVO = (PeriodGroupBuyDetailVO) response.get("groupBuyDetail");
        System.out.println(detailVO.toString());
        System.out.println("기간 공동구매 번호: " + detailVO.getPeriodGroupBuyNo());
        System.out.println("공동구매 번호: " + detailVO.getGroupBuyNo());
        System.out.println("제목: " + detailVO.getTitle());
        System.out.println("내용: " + (detailVO.getContent() != null ? detailVO.getContent() : "내용 없음"));
        System.out.println("재료 번호: " + detailVO.getIngredientNo());
        System.out.println("상태: " + (detailVO.getStatus() != null ? detailVO.getStatus() : "상태 정보 없음"));
        System.out.println("최대 참여자: " + detailVO.getMaxParticipants());
        System.out.println("현재 참여자: " + detailVO.getParticipants());
        System.out.println("마감일: " + (detailVO.getDueDate() != null ? detailVO.getDueDate() : "마감일 정보 없음"));
        System.out.println("나눔 장소: " + (detailVO.getShareLocation() != null ? detailVO.getShareLocation() : "장소 정보 없음"));
        System.out.println("나눔 상세 주소: " + (detailVO.getShareDetailAddress() != null ? detailVO.getShareDetailAddress() : "상세 주소 없음"));
        System.out.println("수수료율: " + detailVO.getFeeRate() + "%");
        System.out.println("이미지 URL: " + (detailVO.getImageUrl() != null ? detailVO.getImageUrl() : "이미지 없음"));
        System.out.println("판매 URL: " + (detailVO.getItemSaleUrl() != null ? detailVO.getItemSaleUrl() : "판매 URL 없음"));

        // 2. 참여자 목록 출력
        System.out.println("\n========== 참여자 목록 ==========");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> participants = (List<Map<String, Object>>) response.get("participants");
        System.out.println("총 참여자 수: " + participants.size());

        if (participants.isEmpty()) {
            System.out.println("아직 참여자가 없습니다.");
        } else {
            for (int i = 0; i < participants.size(); i++) {
                Map<String, Object> participant = participants.get(i);
                System.out.printf("[%d] 닉네임: %-10s | 참여일: %s | 프로필 URL: %s\n",
                        i + 1,
                        participant.get("nickname") != null ? participant.get("nickname") : "익명",
                        participant.get("participatedDate") != null ? participant.get("participatedDate") : "참여일 정보 없음",
                        participant.get("profileUrl") != null ? participant.get("profileUrl") : "프로필 이미지 없음");
            }
        }

        // 3. 관련 레시피 목록 출력
        System.out.println("관련 레시피 목록");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> recipes = (List<Map<String, Object>>) response.get("recipes");
        System.out.println("총 레시피 수: " + recipes.size());

        if (recipes.isEmpty()) {
            System.out.println("아직 관련 레시피가 없습니다.");
        } else {
            for (int i = 0; i < recipes.size(); i++) {
                Map<String, Object> recipe = recipes.get(i);
                System.out.printf("[%d] 제목: %-20s | 작성자: %-10s | 등록일: %s | 이미지 URL: %s\n",
                        i + 1,
                        recipe.get("title") != null ? recipe.get("title") : "제목 없음",
                        recipe.get("authorNickname") != null ? recipe.get("authorNickname") : "익명",
                        recipe.get("inDate") != null ? recipe.get("inDate") : "등록일 정보 없음",
                        recipe.get("imageUrl") != null ? recipe.get("imageUrl") : "이미지 없음");
            }
        }

        // 4. 전체 요약
        System.out.println("조회 요약");
        System.out.println("공동구매 번호: " + testPeriodGroupBuyNo);
        System.out.println("공동구매 제목: " + (detailVO.getTitle() != null ? detailVO.getTitle() : "제목 없음"));
        System.out.println("참여자 수: " + participants.size() + "명");
        System.out.println("관련 레시피 수: " + recipes.size() + "개");
        System.out.println("공동구매 상태: " + (detailVO.getStatus() != null ? detailVO.getStatus() : "상태 정보 없음"));

        if (participants.isEmpty()) {
            System.out.println("→ 아직 참여자가 없는 공동구매입니다.");
        }
        if (recipes.isEmpty()) {
            System.out.println("→ 해당 재료로 만든 레시피가 아직 없습니다.");
        }
    }

}
