package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.vo.RecipeListVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class RecipeRepositoryTest {

    @Autowired
    RecipeRepository recipeRepository;

    @Test
    void findAllByDelDateIsNullOrderByRecipeNoDesc() {
        List<Recipe> entities = recipeRepository.findAllByDelDateIsNullOrderByRecipeNoDesc();

        // 3. [VO 변환] Service가 할 일을 테스트에서 직접 수행
        List<RecipeListVO> voList = new ArrayList<>();

        for (Recipe recipe : entities) {
            RecipeListVO vo = new RecipeListVO();

            // Entity -> VO 데이터 옮기기
            vo.setRecipeNo(recipe.getRecipeNo());
            vo.setTitle(recipe.getTitle());
            vo.setThumbnailUrl(recipe.getImageUrl());

            // ★ 핵심: Member(작성자) 정보가 잘 로딩되는지 확인
            // @EntityGraph가 없으면 여기서 추가 쿼리가 나갈 수 있음 (성능 체크)
            if (recipe.getAuthor() != null) {
                vo.setWriterNickname(recipe.getAuthor().getNickname());
                vo.setWriterProfile(recipe.getAuthor().getImageUrl());
            }

            vo.setRating(recipe.getAverageRating());
            vo.setReviewCount(recipe.getReviewCount());
            vo.setServings(recipe.getServings());
            vo.setPrepTime(recipe.getPrepTime());
            vo.setDifficulty(recipe.getDifficulty());
            vo.setSpicyLevel(recipe.getSpicyLevel()); // 맵기 (변환 필요 시)

            // 리스트에 추가
            voList.add(vo);
        }

        // 4. [눈으로 확인] 변환된 VO 데이터 출력
        System.out.println("========== 변환된 VO 목록 출력 ==========");
        for (RecipeListVO vo : voList) {
            System.out.println(vo); // VO에 @Data(ToString)가 있어야 예쁘게 나옵니다.
        }
        System.out.println("=======================================");


    }


}



