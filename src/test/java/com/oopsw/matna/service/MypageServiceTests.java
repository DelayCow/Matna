package com.oopsw.matna.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MypageServiceTests {
    @Autowired
    private MypageService mypageService;

    @Test
    void getMyPageRecipeListTest(){
        int memberNo = 15;
        System.out.println(mypageService.getMypageRecipeList(memberNo));
    }
}
