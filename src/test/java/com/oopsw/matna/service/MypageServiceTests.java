package com.oopsw.matna.service;

import com.oopsw.matna.vo.RecipeVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MypageServiceTests {
    @Autowired
    private MypageService mypageService;

    @Test
    public void getMypageRecipeList() {
        List<RecipeVO> recipeList = mypageService.getMypageRecipeList(15);
        System.out.println(recipeList);
    }
}
