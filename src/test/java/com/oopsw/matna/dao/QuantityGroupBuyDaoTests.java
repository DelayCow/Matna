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
    void getGroupBuyListRecentOrderTest() {
        Map<String, Object> params = new HashMap<>();
        List<QuantityGroupBuyHomeVO> list = quantityGroupBuyDAO.selectQuantityGroupBuyHomeList(params);

        for (QuantityGroupBuyHomeVO vo : list) {
            System.out.println(vo.toString());
        }
    }

    @Test
    //마감 임박순 정렬 테스트 (남은 수량 비율 기준)")
    void getGroupBuyListRemainingOrderTest() {
        Map<String, Object> params = new HashMap<>();
        params.put("orderBy", "remaining");
        List<QuantityGroupBuyHomeVO> list = quantityGroupBuyDAO.selectQuantityGroupBuyHomeList(params);

        Comparator<QuantityGroupBuyHomeVO> remainingRatioComparator = (vo1, vo2) -> {
            double denominator1 = vo1.getQuantity() - vo1.getMyQuantity();
            double denominator2 = vo2.getQuantity() - vo2.getMyQuantity();

            double ratio1 = (denominator1 <= 0)
                    ? Double.MAX_VALUE
                    : (double) vo1.getRemainingQty() / denominator1;

            double ratio2 = (denominator2 <= 0)
                    ? Double.MAX_VALUE
                    : (double) vo2.getRemainingQty() / denominator2;
            return Double.compare(ratio1, ratio2);
        };

        for (int i = 0; i < list.size() - 1; i++) {
            QuantityGroupBuyHomeVO current = list.get(i);
            QuantityGroupBuyHomeVO next = list.get(i + 1);
        }


        for (QuantityGroupBuyHomeVO vo : list) {
            int remQty = vo.getRemainingQty();
            int myQty = vo.getMyQuantity();
            int qty = vo.getQuantity();
            int requiredQty = qty - myQty;
            double ratio = (requiredQty > 0) ? (double) remQty / requiredQty : -1.0;

            System.out.printf("Ratio: %.4f (남은 수량 %d / 필요 수량 %d) | %s\n",
                    ratio, remQty, requiredQty, vo.toString());
        }
    }

    @Test
    //검색 기능 테스트 (제목/재료명/나눔장소)")
    void getGroupBuyListWithKeywordTest() {
        String testKeyword = "양배추";
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", testKeyword);
        List<QuantityGroupBuyHomeVO> list = quantityGroupBuyDAO.selectQuantityGroupBuyHomeList(params);

        for (QuantityGroupBuyHomeVO vo : list) {
            System.out.println(vo.toString());
        }
    }

    @Test
    void getQuantityGroupBuyDetailTest(){
        Integer testQuantityGroupBuyNo = 17;
        QuantityGroupBuyDetailVO detailVO = quantityGroupBuyDAO.selectQuantityGroupBuyDetail(testQuantityGroupBuyNo);

        System.out.println(detailVO.toString());
    }

    @Test
    void getQuantityDetailInParticipantTest() {
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
    void getQuantityDetailInRecipesTest(){
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
