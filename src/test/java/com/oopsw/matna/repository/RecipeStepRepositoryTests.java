package com.oopsw.matna.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RecipeStepRepositoryTests {

    @Autowired
    RecipeStepRepository recipeStepRepository;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    MemberRepository memberRepository;

//    @Test
//    Member writer = Member.builder()
//            .memberId("Step_test")
//            .password("1234")
//            .nickname("테스트닉네임")
//            .roll("USER")
//            .point(0)
//            .inDate(LocalDateTime.now())
//            .build();
         //memberRepository.save(writer);
}
