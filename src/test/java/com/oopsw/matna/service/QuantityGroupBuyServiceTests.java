package com.oopsw.matna.service;

import com.oopsw.matna.controller.groupbuy.GroupBuyParticipantRequest;
import com.oopsw.matna.controller.groupbuy.QuantityRegisterRequest;
import com.oopsw.matna.repository.*;
import com.oopsw.matna.repository.entity.GroupBuy;
import com.oopsw.matna.repository.entity.GroupBuyParticipant;
import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.repository.entity.QuantityGroupBuy;
import com.oopsw.matna.vo.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Transactional
public class QuantityGroupBuyServiceTests {
    @Autowired
    private QuantityGroupBuyService quantityGroupBuyService;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private GroupBuyRepository groupBuyRepository;
    @Autowired
    private QuantityGroupBuyRepository quantityGroupBuyRepository;
    @Autowired
    private GroupBuyParticipantRepository groupBuyParticipantRepository;

    @Test
    @Transactional
    void addQuantityGroupBuyTest() throws IOException {
        Integer ingredientNo = 45;
        Integer creatorNo = 16;
        // 재료 존재 여부 확인
        ingredientRepository.findById(ingredientNo)
                .orElseThrow(() -> new AssertionError("테스트용 재료(ingredientNo: " + ingredientNo + ")가 존재하지 않습니다."));
        // 회원 존재 여부 확인
        memberRepository.findById(creatorNo)
                .orElseThrow(() -> new AssertionError("테스트용 회원(memberNo: " + creatorNo + ")이 존재하지 않습니다."));

        // VO 생성
        QuantityRegisterRequest request = QuantityRegisterRequest.builder()
                .ingredientNo(ingredientNo)
                .creatorNo(creatorNo)
                .title("양배추 4통 같이 나눌분")
                .buyEndDate(3)
                .shareEndDate(2)
                .shareTime(LocalTime.parse("18:00"))
                .shareLocation("서울 금천구 가산디지털1로 70")
                .shareDetailAddress("호서대벤처 1층입구")
                .price(12000)
                .quantity(4)
                .unit("개")
                .feeRate(5)
                .imageUrl("http://example.com/image_path/cabbage.jpg")
                .content("양배추가 저렴한데 너무 많아요 같이사실분")
                .itemSaleUrl("http://sale.site/item/12345")
                .myQuantity(1)
                .shareAmount(1)
                .build();

        // Mock 이미지 파일 생성
        MockMultipartFile mockFile = new MockMultipartFile(
                "thumbnailFile",
                "goguma.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // 예상 단위당 가격 계산
        int expectedPricePerUnit = (int) Math.round((request.getPrice() * (1.0 + (request.getFeeRate() / 100.0))) / request.getQuantity());

        QuantityGroupBuy result = quantityGroupBuyService.addQuantityGroupBuy(request, mockFile);
        assertNotNull(result);
        assertNotNull(result.getQuantityGroupBuyNo());
        assertNotNull(result.getGroupBuy());

        // GroupBuy 검증
        GroupBuy groupBuy = result.getGroupBuy();
        assertEquals("양배추 4통 같이 나눌분", groupBuy.getTitle());
        assertEquals(ingredientNo, groupBuy.getIngredient().getIngredientNo());
        assertEquals(creatorNo, groupBuy.getCreator().getMemberNo());
        assertEquals(12000, groupBuy.getPrice());
        assertEquals(4, groupBuy.getQuantity());
        assertEquals("개", groupBuy.getUnit());
        assertEquals("open", groupBuy.getStatus());
        // QuantityGroupBuy 검증
        assertEquals(1, result.getMyQuantity());
        assertEquals(1, result.getShareAmount());
        assertEquals(expectedPricePerUnit, result.getPricePerUnit());


        System.out.println("제목: " + groupBuy.getTitle());
        System.out.println("총 가격: " + groupBuy.getPrice() + "원");
        System.out.println("총 수량: " + groupBuy.getQuantity() + groupBuy.getUnit());
        System.out.println("수수료율: " + groupBuy.getFeeRate() + "%");
        System.out.println("개설자 부담 수량: " + result.getMyQuantity() + groupBuy.getUnit());
        System.out.println("나눔 단위: " + result.getShareAmount() + groupBuy.getUnit());
        System.out.println("계산식: (" + groupBuy.getPrice() + " * (1 + " + groupBuy.getFeeRate() + "/100)) / " + groupBuy.getQuantity());
        System.out.println("단위당 가격: " + result.getPricePerUnit() + "원/" + groupBuy.getUnit());
    }

    @Test
    void addParticipantToQuantityGroupBuyTest() {
        // 테스트 데이터
        Integer participantNo = 12;
        Integer groupBuyNo = 20;
        Integer participantQuantity = 1000; // shareAmount의 배수여야 함

        // 참여 전 회원 포인트
        Member participantMember = memberRepository.findById(participantNo).get();
        int beforePoint = participantMember.getPoint();

        // 공동구매 및 수량공구 정보
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyNo).get();
        QuantityGroupBuy quantityGroupBuy = quantityGroupBuyRepository.findByGroupBuy(groupBuy);

        // Request 생성
        GroupBuyParticipantRequest request = GroupBuyParticipantRequest.builder()
                .participantNo(participantNo)
                .groupBuyNo(groupBuyNo)
                .myQuantity(participantQuantity)
                .build();

        // 예상 결제 금액 계산
        int shareAmount = quantityGroupBuy.getShareAmount();
        int pricePerUnit = quantityGroupBuy.getPricePerUnit();
        int shareUnits = participantQuantity / shareAmount;
        int expectedPayment = shareUnits * pricePerUnit;

        // 서비스 호출
        GroupBuyParticipant result = quantityGroupBuyService.addParticipantToQuantityGroupBuy(request);

        // 검증
        assertNotNull(result);
        assertEquals(participantNo, result.getParticipant().getMemberNo());
        assertEquals(groupBuyNo, result.getGroupBuy().getGroupBuyNo());
        assertEquals(participantQuantity, result.getMyQuantity());
        assertEquals(expectedPayment, result.getInitialPaymentPoint());
        assertNotNull(result.getParticipatedDate());

        // 포인트 차감 확인
        Member updatedMember = memberRepository.findById(participantNo).get();
        assertEquals(beforePoint - expectedPayment, updatedMember.getPoint());

        System.out.println("=== 수량공구 참여 성공 ===");
        System.out.println("참여자 번호: " + result.getParticipant().getMemberNo());
        System.out.println("공구 번호: " + result.getGroupBuy().getGroupBuyNo());
        System.out.println("참여 수량: " + result.getMyQuantity() + "개/g");
        System.out.println("나눔 단위: " + shareAmount + "개/g");
        System.out.println("단위 수: " + shareUnits);
        System.out.println("단위당 가격: " + pricePerUnit + "원");
        System.out.println("결제 금액: " + result.getInitialPaymentPoint() + "원");
        System.out.println("참여 전 포인트: " + beforePoint + "P");
        System.out.println("참여 후 포인트: " + updatedMember.getPoint() + "P");
    }

    @Test
    void editModifyMyQuantityTest() {
        // 테스트 데이터
        Integer participantNo = 19;
        Integer groupBuyParticipantNo = 59;
        int newQuantity = 1000; // 수정할 새로운 수량

        // 참여자 및 참여 정보 조회
        Member participantMember = memberRepository.findById(participantNo).get();
        GroupBuyParticipant groupBuyParticipant = groupBuyParticipantRepository.findById(groupBuyParticipantNo).get();

        // 수정 전 데이터
        int initialMyQuantity = groupBuyParticipant.getMyQuantity();
        int beforePoint = participantMember.getPoint();
        int beforeInitialPayment = groupBuyParticipant.getInitialPaymentPoint();

        // 공동구매 및 수량공구 정보
        GroupBuy groupBuy = groupBuyParticipant.getGroupBuy();
        QuantityGroupBuy quantityGroupBuy = quantityGroupBuyRepository.findByGroupBuy(groupBuy);
        int pricePerUnit = quantityGroupBuy.getPricePerUnit();
        int shareAmount = quantityGroupBuy.getShareAmount();

        // 새로운 지불 금액 계산
        int shareUnits = newQuantity / shareAmount;
        int newPayment = shareUnits * pricePerUnit;
        int modifyPoint = newPayment - beforeInitialPayment; // 양수: 추가 지불, 음수: 환불

        // 포인트 계산
        int expectedNewPoint = beforePoint - modifyPoint; // 추가 지불 시 차감, 환불 시 증가

        // 서비스 호출
        quantityGroupBuyService.editModifyMyQuantity(groupBuyParticipantNo, participantNo, newQuantity);

        // 검증
        GroupBuyParticipant updatedParticipant = groupBuyParticipantRepository.findById(groupBuyParticipantNo).get();
        Member updatedMember = memberRepository.findById(participantNo).get();

        assertEquals(newQuantity, updatedParticipant.getMyQuantity(), "수량이 올바르게 수정되어야 합니다.");
        assertEquals(newPayment, updatedParticipant.getInitialPaymentPoint(), "결제 금액이 올바르게 수정되어야 합니다.");
        assertEquals(expectedNewPoint, updatedMember.getPoint(), "포인트가 올바르게 계산되어야 합니다.");

        // 결과 출력
        System.out.println("=== 수량 수정 결과 ===");
        System.out.println("기존 구매 수량: " + initialMyQuantity + "개/g");
        System.out.println("수정 구매 수량: " + newQuantity + "개/g");
        System.out.println("수량 차이: " + (newQuantity - initialMyQuantity) + "개/g");
        System.out.println("나눔 단위: " + shareAmount + "개/g");
        System.out.println("단위당 가격: " + pricePerUnit + "원");
        System.out.println("기존 결제 금액: " + beforeInitialPayment + "P");
        System.out.println("새로운 결제 금액: " + newPayment + "P");
        System.out.println("차액 (추가지불/환불): " + modifyPoint + "P" +
                (modifyPoint > 0 ? " (추가 지불)" : modifyPoint < 0 ? " (환불)" : " (변동 없음)"));
        System.out.println("수정 전 포인트: " + beforePoint + "P");
        System.out.println("수정 후 포인트: " + updatedMember.getPoint() + "P");
        System.out.println("포인트 변동: " + (updatedMember.getPoint() - beforePoint) + "P");
    }


    @Test
    void editForceCloseQuantityGroupBuyTest() {
        Integer groupBuyNo = 20;
        Integer creatorNo = 17;
        // 공동구매 존재 여부 확인
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyNo)
                .orElseThrow(() -> new AssertionError("테스트용 공동구매(groupBuyNo: " + groupBuyNo + ")가 존재하지 않습니다."));
        // QuantityGroupBuy 조회
        QuantityGroupBuy quantityGroupBuy = quantityGroupBuyRepository.findByGroupBuy(groupBuy);
        assertNotNull(quantityGroupBuy, "QuantityGroupBuy가 존재해야 합니다.");

        // 이미 마감된 경우 테스트 스킵
        if ("closed".equals(groupBuy.getStatus())) {
            System.out.println("이미 마감된 공동구매입니다. 상태: " + groupBuy.getStatus());
            return;
        }
        // 강제 마감 전 상태 저장
        int initialMyQuantity = quantityGroupBuy.getMyQuantity();
        int totalQuantity = groupBuy.getQuantity();

        // 현재 수량 계산
        List<GroupBuyParticipant> participants = groupBuyParticipantRepository.findByGroupBuy(groupBuy);
        int currentSharedQuantity = 0;
        for (GroupBuyParticipant participant : participants) {
            if (participant.getCancelDate() == null && participant.getMyQuantity() != null) {
                currentSharedQuantity += participant.getMyQuantity();
            }
        }

        int remainingQuantity = totalQuantity - (currentSharedQuantity + initialMyQuantity);
        // 이미 수량이 충족된 경우 테스트 스킵
        if (remainingQuantity <= 0) {
            System.out.println("이미 수량이 충족되어 강제 마감이 필요하지 않습니다.");
            return;
        }

        System.out.println("총 필요 수량: " + totalQuantity + groupBuy.getUnit());
        System.out.println("개설자 초기 부담 수량: " + initialMyQuantity + groupBuy.getUnit());
        System.out.println("현재 공유 수량: " + currentSharedQuantity + groupBuy.getUnit());
        System.out.println("남은 수량: " + remainingQuantity + groupBuy.getUnit());
        System.out.println("공동구매 상태: " + groupBuy.getStatus());

        QuantityGroupBuy result = quantityGroupBuyService.editForcedCreatorAndStatusToClosed(groupBuyNo, creatorNo);
        assertNotNull(result);

        // 개설자 부담 수량 확인
        int expectedMyQuantity = initialMyQuantity + remainingQuantity;
        assertEquals(expectedMyQuantity, result.getMyQuantity(),
                "개설자 부담 수량이 잔여 수량만큼 증가해야 합니다.");

        // 공동구매 상태 확인
        GroupBuy updatedGroupBuy = groupBuyRepository.findById(groupBuyNo).get();
        assertEquals("closed", updatedGroupBuy.getStatus(),
                "공동구매 상태가 'closed'여야 합니다.");

        System.out.println("공동구매 상태: " + updatedGroupBuy.getStatus());
        System.out.println("개설자 최종 부담 수량: " + result.getMyQuantity() + groupBuy.getUnit());
        System.out.println("추가 부담 수량: " + remainingQuantity + groupBuy.getUnit());

        // 수량 충족 검증
        int finalTotalQuantity = result.getMyQuantity() + currentSharedQuantity;
        assertEquals(totalQuantity, finalTotalQuantity,
                "최종 확보 수량이 총 필요 수량과 같아야 합니다.");

        System.out.println("개설자가 남은 수량(" + remainingQuantity + groupBuy.getUnit() + ")을 부담하여 공동구매가 마감되었습니다.");
    }

    @Test
    void editQuantityCreatorCancelAndRefundTest() {
        Integer creatorMemberNo = 17;
        Integer groupBuyNo = 20;
        String cancelReason = "가격변동";
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
                        () -> quantityGroupBuyService.editQuantityCreatorCancelAndRefund(groupBuyNo, creatorMemberNo, cancelReason)
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


            quantityGroupBuyService.editQuantityCreatorCancelAndRefund(groupBuyNo, creatorMemberNo, cancelReason);
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
    void getQuantityGroupBuyHomeVariousConditionsTest() {
        // 1. 최신 등록순
        System.out.println("1. 최신 등록순");
        Map<String, Object> params1 = new HashMap<>();
        List<QuantityGroupBuyHomeVO> list1 = quantityGroupBuyService.getQuantityGroupBuyHome(params1);
        assertNotNull(list1);
        System.out.println("조회 건수: " + list1.size());

        // 2. 마감 임박순
        System.out.println("2. 마감 임박순 ");
        Map<String, Object> params2 = new HashMap<>();
        params2.put("orderBy", "dueSoon");
        List<QuantityGroupBuyHomeVO> list2 = quantityGroupBuyService.getQuantityGroupBuyHome(params2);
        assertNotNull(list2);
        System.out.println("조회 건수: " + list2.size());

        // 3. 키워드 검색 - "쌀"
        System.out.println("3. 키워드 검색: '쌀'");
        Map<String, Object> params3 = new HashMap<>();
        params3.put("keyword", "쌀");
        List<QuantityGroupBuyHomeVO> list3 = quantityGroupBuyService.getQuantityGroupBuyHome(params3);
        assertNotNull(list3);
        System.out.println("조회 건수: " + list3.size());

        // 4. 키워드 검색 + 마감 임박순
        System.out.println("4. 키워드 검색 + 마감 임박순: '고구마'");
        Map<String, Object> params4 = new HashMap<>();
        params4.put("keyword", "고구마");
        params4.put("orderBy", "deadline");
        List<QuantityGroupBuyHomeVO> list4 = quantityGroupBuyService.getQuantityGroupBuyHome(params4);
        assertNotNull(list4);
        System.out.println("조회 건수: " + list4.size());
        if (!list4.isEmpty()) {
            System.out.println("첫 번째 결과: " + list4.get(0).getTitle());
        }
        // 5. null params
        System.out.println("5. null params (기본 조회)");
        List<QuantityGroupBuyHomeVO> list5 = quantityGroupBuyService.getQuantityGroupBuyHome(null);
        assertNotNull(list5);
        System.out.println("조회 건수: " + list5.size());
    }

    @Test
    void getQuantityGroupBuyDetailTest() {
        Integer testQuantityGroupBuyNo = 12;

        Map<String, Object> response = quantityGroupBuyService.getQuantityGroupBuyDetail(testQuantityGroupBuyNo);
        assertNotNull(response, "응답 객체가 null이 아니어야 합니다.");
        assertNotNull(response.get("groupBuyDetail"), "공동구매 상세 정보가 null이 아니어야 합니다.");
        assertNotNull(response.get("participants"), "참여자 목록이 null이 아니어야 합니다.");
        assertNotNull(response.get("recipes"), "레시피 목록이 null이 아니어야 합니다.");

        // 1. 공동구매 상세 정보 출력
        System.out.println(" 상세 정보");
        QuantityGroupBuyDetailVO detailVO = (QuantityGroupBuyDetailVO) response.get("groupBuyDetail");
        System.out.println(detailVO.toString());
        System.out.println("기간 공동구매 번호: " + detailVO.getQuantityGroupBuyNo());
        System.out.println("공동구매 번호: " + detailVO.getGroupBuyNo());
        System.out.println("제목: " + detailVO.getTitle());
        System.out.println("내용: " + (detailVO.getContent() != null ? detailVO.getContent() : "내용 없음"));
        System.out.println("재료 번호: " + detailVO.getIngredientNo());
        System.out.println("상태: " + (detailVO.getStatus() != null ? detailVO.getStatus() : "상태 정보 없음"));
        System.out.println("개설자 수량: " + (detailVO.getMyQuantity()!= null ? detailVO.getMyQuantity() : "개설자 수량 없음"));
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
        System.out.println("공동구매 번호: " + testQuantityGroupBuyNo);
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
