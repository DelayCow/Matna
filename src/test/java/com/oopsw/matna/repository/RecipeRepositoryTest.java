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


            if (recipe.getAuthor() != null) {
                vo.setWriterNickname(recipe.getAuthor().getNickname());
                vo.setWriterProfile(recipe.getAuthor().getImageUrl());
            }

            vo.setRating(recipe.getAverageRating());
            vo.setReviewCount(recipe.getReviewCount());
            vo.setServings(recipe.getServings());
            vo.setPrepTime(recipe.getPrepTime());
            vo.setDifficulty(recipe.getDifficulty());
            vo.setSpicyLevel(recipe.getSpicyLevel());

            // 리스트에 추가
            voList.add(vo);
        }

        // 4. [눈으로 확인] 변환된 VO 데이터 출력
        System.out.println("========== 변환된 VO 목록 출력 ==========");
        for (RecipeListVO vo : voList) {
            System.out.println(vo);
        }
        System.out.println("=======================================");


    }


}



