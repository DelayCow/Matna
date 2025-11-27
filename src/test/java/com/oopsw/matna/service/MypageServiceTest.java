package com.oopsw.matna.service;

import com.oopsw.matna.repository.RecipeRepository;
import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.vo.RecipeVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MypageServiceTest {

    @Autowired
    private MypageService mypageService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    void MypageTest() {

        List<RecipeVO> recipeVOList = mypageService.getMyPageRecipeList(15);
        System.out.println(recipeVOList);
    }
}
