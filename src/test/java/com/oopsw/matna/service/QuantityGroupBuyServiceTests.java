package com.oopsw.matna.service;

import com.oopsw.matna.repository.*;
import com.oopsw.matna.repository.entity.GroupBuy;
import com.oopsw.matna.repository.entity.GroupBuyParticipant;
import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.repository.entity.QuantityGroupBuy;
import com.oopsw.matna.vo.GroupBuyParticipantVO;
import com.oopsw.matna.vo.PeriodGroupBuyHomeVO;
import com.oopsw.matna.vo.QuantityGroupBuyCreateVO;
import com.oopsw.matna.vo.QuantityGroupBuyHomeVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
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
    void addQuantityGroupBuyTest() {
        Integer ingredientNo = 45;
        Integer creatorNo = 16;
        // 재료 존재 여부 확인
        ingredientRepository.findById(ingredientNo)
                .orElseThrow(() -> new AssertionError("테스트용 재료(ingredientNo: " + ingredientNo + ")가 존재하지 않습니다."));
        // 회원 존재 여부 확인
        memberRepository.findById(creatorNo)
                .orElseThrow(() -> new AssertionError("테스트용 회원(memberNo: " + creatorNo + ")이 존재하지 않습니다."));

        // VO 생성
        QuantityGroupBuyCreateVO vo = QuantityGroupBuyCreateVO.builder()
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

        // 예상 단위당 가격 계산
        int expectedPricePerUnit = (int) Math.round((vo.getPrice() * (1.0 + (vo.getFeeRate() / 100.0))) / vo.getQuantity());

        QuantityGroupBuy result = quantityGroupBuyService.addQuantityGroupBuy(vo);
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
        Integer participantNo = 12;
        Integer groupBuyNo = 32;
        // 참여자 존재 여부 확인
        Member member = memberRepository.findById(participantNo)
                .orElseThrow(() -> new AssertionError("테스트용 회원(memberNo: " + participantNo + ")이 존재하지 않습니다."));
        // 공동구매 존재 여부 확인
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyNo)
                .orElseThrow(() -> new AssertionError("테스트용 공동구매(groupBuyNo: " + groupBuyNo + ")가 존재하지 않습니다."));
        // QuantityGroupBuy 조회
        QuantityGroupBuy quantityGroupBuy = quantityGroupBuyRepository.findByGroupBuy(groupBuy);
        assertNotNull(quantityGroupBuy, "QuantityGroupBuy가 존재해야 합니다.");

        // 포인트 부족 확인
        Integer price = groupBuy.getPrice();
        Integer feeRate = groupBuy.getFeeRate();
        int expectedPayment = (int) Math.round((price * (1.0 + (feeRate / 100.0))) / 2.0);

        if (member.getPoint() < expectedPayment) {
            System.out.println("포인트 부족: 현재 " + member.getPoint() + "원, 필요 " + expectedPayment + "원");
            return;
        }

        // VO 생성
        GroupBuyParticipantVO vo = GroupBuyParticipantVO.builder()
                .participantNo(participantNo)
                .groupBuyNo(groupBuyNo)
                .build();

        GroupBuyParticipant result = quantityGroupBuyService.addParticipantToQuantityGroupBuy(vo);
        assertNotNull(result);

        // 수량 충족 여부 확인
        GroupBuy updatedGroupBuy = groupBuyRepository.findById(groupBuyNo).get();

        List<GroupBuyParticipant> participants = groupBuyParticipantRepository.findByGroupBuy(updatedGroupBuy);
        int sharedQuantity = 0;
        for (GroupBuyParticipant p : participants) {
            if (p.getCancelDate() == null && p.getMyQuantity() != null) {
                sharedQuantity += p.getMyQuantity();
            }
        }

        int totalQuantity = groupBuy.getQuantity();
        int myQuantity = quantityGroupBuy.getMyQuantity();
        boolean isQuantitySatisfied = (sharedQuantity + myQuantity >= totalQuantity);

        System.out.println("총 필요 수량: " + totalQuantity + groupBuy.getUnit());
        System.out.println("총 확보 수량: " + (sharedQuantity + myQuantity) + groupBuy.getUnit());
        System.out.println("수량 충족: " + isQuantitySatisfied);
        System.out.println("공동구매 상태: " + updatedGroupBuy.getStatus());

        if (isQuantitySatisfied) {
            assertEquals("closed", updatedGroupBuy.getStatus(), "수량 충족 시 상태가 'closed'여야 합니다.");
        } else {
            assertEquals("open", updatedGroupBuy.getStatus(), "수량 미충족 시 상태가 'open'이어야 합니다.");
        }
    }

    @Test
    void forceCloseQuantityGroupBuyTest() {
        Integer groupBuyNo = 32;
        Integer creatorNo = 16;
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

        QuantityGroupBuy result = quantityGroupBuyService.editForceCloseQuantityGroupBuy(groupBuyNo, creatorNo);
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
}
