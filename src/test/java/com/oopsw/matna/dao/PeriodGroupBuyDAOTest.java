package com.oopsw.matna.dao;

import com.oopsw.matna.vo.PeriodGroupBuyDetailVO;
import com.oopsw.matna.vo.PeriodGroupBuyHomeVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PeriodGroupBuyDAOTest {
    @Autowired
    private PeriodGroupBuyDAO periodGroupBuyDAO;

    @Test
    @DisplayName("1. 최신 등록순 정렬 테스트 및 기본 필드 매핑 검증")
    void testSelectGroupBuyListRecentOrder() {
        Map<String, Object> params = new HashMap<>();

        List<PeriodGroupBuyHomeVO> list = periodGroupBuyDAO.selectGroupBuyListForHome(params);
        assertFalse(list.isEmpty(), "DB에 OPEN 상태의 공동구매 데이터가 존재해야 합니다.");

        if (list.size() > 1) {
            LocalDateTime firstInDate = list.get(0).getInDate();
            LocalDateTime secondInDate = list.get(1).getInDate();

            assertTrue(firstInDate.isAfter(secondInDate) || firstInDate.equals(secondInDate),
                    "목록이 최신 등록일(InDate) 순서로 정렬되어야 합니다. (DESC)");
        }

        PeriodGroupBuyHomeVO firstItem = list.get(0);
        assertNotNull(firstItem.getGroupBuyNo(), "GroupBuyNo는 null이 아니어야 합니다.");
        assertFalse(firstItem.getTitle().isBlank(), "Title은 빈 문자열이 아니어야 합니다.");
        assertTrue(firstItem.getParticipants() >= 1, "참여 인원은 최소 1명(생성자) 이상이어야 합니다.");
        assertTrue(firstItem.getMaxParticipants() > 0, "최대 참여 인원은 0보다 커야 합니다.");

        // 새로 추가된 필드의 매핑 검증
        assertNotNull(firstItem.getDueDate(), "모집 마감일(dueDate)은 null이 아니어야 합니다.");
        assertTrue(firstItem.getPurchasePeriodDays() >= 0, "구매 가능 일수(purchasePeriodDays)는 0 이상이어야 합니다.");
        assertNotNull(firstItem.getFinalPurchaseDeadline(), "최종 구매 마감일은 null이 아니어야 합니다.");

        for (PeriodGroupBuyHomeVO vo : list) {
            System.out.println(vo.toString());
        }
    }

    @Test
    @DisplayName("2. 마감 임박순 정렬 테스트 (모집 마감일 dueDate 기준)")
    void testSelectGroupBuyListDeadlineOrder() {
        Map<String, Object> params = new HashMap<>();
        params.put("orderBy", "deadline"); // 마감 임박순 정렬 조건 (dueDate ASC)

        List<PeriodGroupBuyHomeVO> list = periodGroupBuyDAO.selectGroupBuyListForHome(params);

        assertFalse(list.isEmpty(), "DB에 OPEN 상태의 공동구매 데이터가 존재해야 합니다.");

        // 모집 마감일(dueDate)이 가장 빠른 순서(오름차순)로 와야 합니다.
        if (list.size() > 1) {
            LocalDateTime firstDeadline = list.get(0).getDueDate();
            LocalDateTime secondDeadline = list.get(1).getDueDate();

            // 첫 번째 항목의 마감일이 두 번째 항목보다 같거나 빨라야 함 (ASC)
            assertTrue(firstDeadline.isBefore(secondDeadline) || firstDeadline.isEqual(secondDeadline),
                    "목록이 모집 마감일(dueDate ASC) 순서로 정렬되어야 합니다.");
        }

        for (PeriodGroupBuyHomeVO vo : list) {
            System.out.println(vo.toString());
        }
    }

    @Test
    @DisplayName("3. 검색 기능 테스트 (제목/재료명/나눔장소)")
    void testSelectGroupBuyListWithKeyword() {
        String testKeyword = "고구마";
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", testKeyword);

        List<PeriodGroupBuyHomeVO> list = periodGroupBuyDAO.selectGroupBuyListForHome(params);

        for (PeriodGroupBuyHomeVO item : list) {
            String title = item.getTitle();
            String location = item.getShareLocation();
            String ingredientName = item.getIngredientName();

            boolean matches = (title != null && title.contains(testKeyword)) ||
                    (location != null && location.contains(testKeyword)) ||
                    (ingredientName != null && ingredientName.contains(testKeyword));

            assertTrue(matches, "검색 결과는 제목, 나눔장소, 또는 재료명dml 키워드 : '" + testKeyword);
        }

        for (PeriodGroupBuyHomeVO vo : list) {
            System.out.println(vo.toString());
        }
    }

    @Test
    @DisplayName("4. 기간공동구매 상세 조회 테스트 (period_group_buy_no = 14)")
    void testSelectPeriodGroupBuyDetail_ById() {
        Integer testPeriodGroupBuyNo = 14;
        PeriodGroupBuyDetailVO detailVO = periodGroupBuyDAO.selectPeriodGroupBuyDetail(testPeriodGroupBuyNo);

        assertNotNull(detailVO, "periodGroupBuyNo = " + testPeriodGroupBuyNo);

        // 필수 필드 검증
        // GroupBuyNo와 PeriodGroupBuyNo가 분리되었으므로, PeriodGroupBuyNo 검증 추가
        assertEquals(testPeriodGroupBuyNo.intValue(), detailVO.getPeriodGroupBuyNo(), "조회된 PeriodGroupBuyNo가 14와 일치해야 합니다.");
        assertNotNull(detailVO.getGroupBuyNo(), "GroupBuyNo는 null이 아니어야 합니다.");
        assertFalse(detailVO.getTitle().isBlank(), "공동구매 제목은 비어있지 않아야 합니다.");
        assertTrue(detailVO.getParticipants() >= 1, "참여 인원은 최소 1명(개설자) 이상이어야 합니다.");
        assertNotNull(detailVO.getDueDate(), "모집 마감일(dueDate)은 null이 아니어야 합니다.");
        assertNotNull(detailVO.getRemainingTime(), "남은 시간(remainingTime)은 null이 아니어야 합니다."); // VO 필드명에 맞게 remainingTime으로 수정

        System.out.println(detailVO.toString());
    }
}
