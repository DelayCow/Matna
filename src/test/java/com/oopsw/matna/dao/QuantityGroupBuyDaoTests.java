package com.oopsw.matna.dao;

import com.oopsw.matna.vo.QuantityGroupBuyHomeVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QuantityGroupBuyDaoTests {
    @Autowired
    private QuantityGroupBuyDAO quantityGroupBuyDAO;

    @Test
    @DisplayName("1. 최신 등록순 정렬 테스트 및 기본 필드 매핑 검증")
    void testSelectGroupBuyListRecentOrder() {
        Map<String, Object> params = new HashMap<>();
        // orderBy 조건이 없을 경우 기본값인 최신 등록순(inDate DESC)으로 정렬됨

        List<QuantityGroupBuyHomeVO> list = quantityGroupBuyDAO.selectQuantityGroupBuyHomeList(params);
        assertFalse(list.isEmpty(), "DB에 OPEN 상태의 수량 공동구매 데이터가 존재해야 합니다.");

        // 1-1. 최신 등록순(InDate DESC) 정렬 검증
        if (list.size() > 1) {
            LocalDateTime firstInDate = list.get(0).getInDate();
            LocalDateTime secondInDate = list.get(1).getInDate();

            assertTrue(firstInDate.isAfter(secondInDate) || firstInDate.equals(secondInDate),
                    "목록이 최신 등록일(InDate) 순서로 정렬되어야 합니다. (DESC)");
        }

        // 1-2. 기본 필드 매핑 검증
        QuantityGroupBuyHomeVO firstItem = list.get(0);
        assertNotNull(firstItem.getGroupBuyNo(), "GroupBuyNo는 null이 아니어야 합니다.");
        assertFalse(firstItem.getTitle().isBlank(), "Title은 빈 문자열이 아니어야 합니다.");
        assertNotNull(firstItem.getInDate(), "등록일(InDate)은 null이 아니어야 합니다.");

        // 1-3. 수량 공구 특정 필드 매핑 검증
        assertTrue(firstItem.getQuantity() > 0, "총 수량(quantity)은 0보다 커야 합니다.");
        assertNotNull(firstItem.getUnit(), "단위(unit)는 null이 아니어야 합니다.");
        assertTrue(firstItem.getPricePerUnit() >= 0, "단위당 가격(pricePerUnit)은 0 이상이어야 합니다.");
        assertTrue(firstItem.getRemainingQty() >= 0, "남은 수량(remainingQty)은 0 이상이어야 합니다.");
        assertTrue(firstItem.getRemainingQty() <= firstItem.getQuantity(), "남은 수량은 총 수량보다 클 수 없습니다.");

        for (QuantityGroupBuyHomeVO vo : list) {
            System.out.println(vo.toString());
        }
    }

    @Test
    @DisplayName("2. 마감 임박순 정렬 테스트 (남은 수량 비율 기준)")
    void testSelectGroupBuyListRemainingOrder() {
        Map<String, Object> params = new HashMap<>();
        params.put("orderBy", "remaining"); // 남은 수량 비율 기준 정렬 조건 (remainingQty ASC)

        List<QuantityGroupBuyHomeVO> list = quantityGroupBuyDAO.selectQuantityGroupBuyHomeList(params);

        assertFalse(list.isEmpty(), "DB에 OPEN 상태의 공동구매 데이터가 존재해야 합니다.");

        // 2-1. 남은 수량 비율(RemainingQty / Quantity) 오름차순 정렬 검증
        if (list.size() > 1) {
            // 남은 수량 비율 계산 헬퍼 함수
            Comparator<QuantityGroupBuyHomeVO> remainingRatioComparator = (vo1, vo2) -> {
                double ratio1 = (double) vo1.getRemainingQty() / vo1.getQuantity();
                double ratio2 = (double) vo2.getRemainingQty() / vo2.getQuantity();
                return Double.compare(ratio1, ratio2);
            };

            // XML 쿼리는 remainingQty만으로 정렬한다고 가정 (RemainingRatio는 테스트에서 계산)
            // 따라서 쿼리가 remainingQty를 오름차순으로 잘 가져왔는지 검증합니다.
            Integer firstRemaining = list.get(0).getRemainingQty();
            Integer secondRemaining = list.get(1).getRemainingQty();

            // 첫 번째 항목의 남은 수량이 두 번째 항목보다 같거나 적어야 함 (ASC)
            assertTrue(firstRemaining <= secondRemaining,
                    "목록이 남은 수량(remainingQty ASC) 순서로 정렬되어야 합니다. 남은 수량이 같을 경우 등록일(InDate DESC) 순서로 와야 합니다.");

            // 추가 검증: 남은 수량 비율이 실제로 오름차순인지 확인 (더 엄격한 검증)
            for (int i = 0; i < list.size() - 1; i++) {
                QuantityGroupBuyHomeVO current = list.get(i);
                QuantityGroupBuyHomeVO next = list.get(i + 1);

                // 남은 수량 비율 계산
                double currentRatio = (double) current.getRemainingQty() / current.getQuantity();
                double nextRatio = (double) next.getRemainingQty() / next.getQuantity();

                assertTrue(currentRatio <= nextRatio,
                        String.format("남은 수량 비율 순서가 잘못되었습니다. 현재 비율: %.4f, 다음 비율: %.4f", currentRatio, nextRatio));
            }
        }

        for (QuantityGroupBuyHomeVO vo : list) {
            double ratio = (double) vo.getRemainingQty() / vo.getQuantity();
            System.out.printf("Ratio: %.4f | %s\n", ratio, vo.toString());
        }
    }

    @Test
    @DisplayName("3. 검색 기능 테스트 (제목/재료명/나눔장소)")
    void testSelectGroupBuyListWithKeyword() {
        String testKeyword = "양배추"; // 실제 DB에 있는 데이터에 맞춰 키워드를 변경해야 합니다.
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", testKeyword);

        List<QuantityGroupBuyHomeVO> list = quantityGroupBuyDAO.selectQuantityGroupBuyHomeList(params);

        assertFalse(list.isEmpty(), "검색 결과가 존재해야 합니다.");

        for (QuantityGroupBuyHomeVO item : list) {
            String title = item.getTitle();
            String location = item.getShareLocation();
            String ingredientName = item.getIngredientName();

            boolean matches = (title != null && title.contains(testKeyword)) ||
                    (location != null && location.contains(testKeyword)) ||
                    (ingredientName != null && ingredientName.contains(testKeyword));

            assertTrue(matches, "검색 결과는 제목, 나눔장소, 또는 재료명에 키워드: '" + testKeyword + "'를 포함해야 합니다.");
        }

        for (QuantityGroupBuyHomeVO vo : list) {
            System.out.println(vo.toString());
        }
    }

}
