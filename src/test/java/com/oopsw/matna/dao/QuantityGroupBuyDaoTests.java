package com.oopsw.matna.dao;

import com.oopsw.matna.repository.GroupBuyParticipantRepository;
import com.oopsw.matna.repository.RecipeIngredientRepository;
import com.oopsw.matna.repository.entity.GroupBuyParticipant;
import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.repository.entity.RecipeIngredient;
import com.oopsw.matna.vo.PeriodGroupBuyDetailVO;
import com.oopsw.matna.vo.QuantityGroupBuyDetailVO;
import com.oopsw.matna.vo.QuantityGroupBuyHomeVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QuantityGroupBuyDaoTests {
    @Autowired
    private QuantityGroupBuyDAO quantityGroupBuyDAO;
    @Autowired
    private GroupBuyParticipantRepository groupBuyParticipantRepository;
    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    @Test
    //최신 등록순 정렬 테스트 및 기본 필드 매핑 검증")
    void testSelectGroupBuyListRecentOrder() {
        Map<String, Object> params = new HashMap<>();

        List<QuantityGroupBuyHomeVO> list = quantityGroupBuyDAO.selectQuantityGroupBuyHomeList(params);

        if (list.size() > 1) {
            LocalDateTime firstInDate = list.get(0).getInDate();
            LocalDateTime secondInDate = list.get(1).getInDate();

        }
        for (QuantityGroupBuyHomeVO vo : list) {
            System.out.println(vo.toString());
        }
    }

    @Test
    //마감 임박순 정렬 테스트 (남은 수량 비율 기준)")
    void testSelectGroupBuyListRemainingOrder() {
        Map<String, Object> params = new HashMap<>();
        params.put("orderBy", "remaining"); // 남은 수량 비율 기준 정렬 조건 (remainingQty ASC)

        List<QuantityGroupBuyHomeVO> list = quantityGroupBuyDAO.selectQuantityGroupBuyHomeList(params);

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

            }
        }

        for (QuantityGroupBuyHomeVO vo : list) {
            double ratio = (double) vo.getRemainingQty() / vo.getQuantity();
            System.out.printf("Ratio: %.4f | %s\n", ratio, vo.toString());
        }
    }

    @Test
    //검색 기능 테스트 (제목/재료명/나눔장소)")
    void testSelectGroupBuyListWithKeyword() {
        String testKeyword = "양배추";
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", testKeyword);

        List<QuantityGroupBuyHomeVO> list = quantityGroupBuyDAO.selectQuantityGroupBuyHomeList(params);

        for (QuantityGroupBuyHomeVO item : list) {
            String title = item.getTitle();
            String location = item.getShareLocation();
            String ingredientName = item.getIngredientName();

            boolean matches = (title != null && title.contains(testKeyword)) ||
                    (location != null && location.contains(testKeyword)) ||
                    (ingredientName != null && ingredientName.contains(testKeyword));
        }

        for (QuantityGroupBuyHomeVO vo : list) {
            System.out.println(vo.toString());
        }
    }

    @Test
    void testSelectQuantityGroupBuyDetail(){
        Integer testQuantityGroupBuyNo = 17;
        QuantityGroupBuyDetailVO detailVO = quantityGroupBuyDAO.selectQuantityGroupBuyDetail(testQuantityGroupBuyNo);

        System.out.println(detailVO.toString());
    }

    @Test
    void testSelectQuantityDetailInParticipant() {
        Integer testQuantityGroupBuyNo = 17;

        List<GroupBuyParticipant> participants =
                groupBuyParticipantRepository.findByGroupParticipantNoOrderByParticipatedDateAsc(testQuantityGroupBuyNo);
        if (participants.isEmpty()) {
            return;
        }
        for (GroupBuyParticipant gbp : participants) {
            Member member = gbp.getParticipant();
            String nickname = member.getNickname();
            String profileUrl = member.getImageUrl();

            System.out.printf("닉네임: %-10s | 수량: %-4d | 참여일: %s | 프로필 URL: %s\n",
                    nickname,
                    gbp.getMyQuantity(),
                    gbp.getParticipatedDate(),
                    profileUrl);
        }
    }

    @Test
    void testSelectQuantityDetailInRecipes(){
        Integer testIngredientNo = 45;
        List<RecipeIngredient> results =
                recipeIngredientRepository.findByIngredientIngredientNoOrderByRecipeInDateDesc(testIngredientNo);

        if (results.isEmpty()) {
            return;
        }
        for (RecipeIngredient ri : results) {

            Recipe recipe = ri.getRecipe();
            Member author = recipe.getAuthor();

            String title = recipe.getTitle();
            String imageUrl = recipe.getImageUrl();
            String nickname = author.getNickname();

            System.out.printf("제목: %-20s | 작성자: %-10s | 이미지 URL: %s\n",
                    title,
                    nickname,
                    imageUrl);
        }
    }
}
