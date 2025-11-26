package com.oopsw.matna.dao;

import com.oopsw.matna.repository.GroupBuyParticipantRepository;
import com.oopsw.matna.repository.RecipeIngredientRepository;
import com.oopsw.matna.repository.entity.GroupBuyParticipant;
import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.repository.entity.RecipeIngredient;
import com.oopsw.matna.vo.PeriodGroupBuyDetailVO;
import com.oopsw.matna.vo.PeriodGroupBuyHomeVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PeriodGroupBuyDAOTests {
    @Autowired
    private PeriodGroupBuyDAO periodGroupBuyDAO;
    @Autowired
    private GroupBuyParticipantRepository groupBuyParticipantRepository;
    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    @Test
    //최신 등록순 정렬 테스트 및 기본 필드 매핑 검증")
    void testSelectGroupBuyListRecentOrder() {
        Map<String, Object> params = new HashMap<>();
        List<PeriodGroupBuyHomeVO> list = periodGroupBuyDAO.selectGroupBuyListForHome(params);

        for (PeriodGroupBuyHomeVO vo : list) {
            System.out.println(vo.toString());
        }
    }

    @Test
    //마감 임박순 정렬 테스트 (모집 마감일 dueDate 기준)")
    void testSelectGroupBuyListDeadlineOrder() {
        Map<String, Object> params = new HashMap<>();
        params.put("orderBy", "deadline"); // 마감 임박순 정렬 조건 (dueDate ASC)
        List<PeriodGroupBuyHomeVO> list = periodGroupBuyDAO.selectGroupBuyListForHome(params);

        for (PeriodGroupBuyHomeVO vo : list) {
            System.out.println(vo.toString());
        }
    }

    @Test
    //검색 기능 테스트 (제목/재료명/나눔장소)")
    void testSelectGroupBuyListWithKeyword() {
        String testKeyword = "고구마";
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", testKeyword);
        List<PeriodGroupBuyHomeVO> list = periodGroupBuyDAO.selectGroupBuyListForHome(params);

        for (PeriodGroupBuyHomeVO vo : list) {
            System.out.println(vo.toString());
        }
    }

    @Test
    //기간공동구매 상세 조회 테스트 (period_group_buy_no = 14)")
    void testSelectPeriodGroupBuyDetail() {
        Integer testPeriodGroupBuyNo = 14;
        PeriodGroupBuyDetailVO detailVO = periodGroupBuyDAO.selectPeriodGroupBuyDetail(testPeriodGroupBuyNo);

        System.out.println(detailVO.toString());
    }

    @Test
    void testSelectPeriodDetailInParticipant(){
        Integer testPeriodGroupBuyNo = 14;

        List<GroupBuyParticipant> participants =
                groupBuyParticipantRepository.findByGroupParticipantNoOrderByParticipatedDateAsc(testPeriodGroupBuyNo);
        if (participants.isEmpty()) {
            return;
        }
        for (GroupBuyParticipant gbp : participants) {
            Member member = gbp.getParticipant();
            String nickname = member.getNickname();
            String profileUrl = member.getImageUrl();

            System.out.printf("닉네임: %-10s | 참여일: %s | 프로필 URL: %s\n",
                    nickname,
                    gbp.getParticipatedDate(),
                    profileUrl);
        }
    }

    @Test
    void testSelectPeriodDetailInRecipes(){
        Integer testIngredientNo = 14;
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
