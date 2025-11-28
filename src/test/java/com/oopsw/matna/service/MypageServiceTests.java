package com.oopsw.matna.service;

import com.oopsw.matna.dto.MemberProfileListResponse;
import com.oopsw.matna.vo.MemberProfileVO;
import com.oopsw.matna.vo.MemberVO;
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
    public void getMypageRecipeListTest() {
        List<RecipeVO> recipeList = mypageService.getMypageRecipeList(15);
        System.out.println(recipeList);
    }

    @Test
    public void getMemberProfileTest() {

        MemberProfileListResponse profile = mypageService.getMypageMember(15);
        System.out.println(profile);
    }

//    @Test
//    public void removeMypageRecipeTest() {
//
//        mypageService.removeMypageRecipe(13);
//
//
//    }
}
