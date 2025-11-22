package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.GroupBuy;
import com.oopsw.matna.repository.entity.Ingredient;
import com.oopsw.matna.repository.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class GroupBuyRepositoryTests {
    @Autowired
    IngredientRepository ingredientRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    GroupBuyRepository groupBuyRepository;

    @Test
    void searchIngredientKeyword(){
        String keyword = "쌀";
        List<Ingredient> results = ingredientRepository.findByIngredientNameContaining(keyword);
        for (Ingredient ingredient : results) {
            System.out.println(ingredient.getIngredientName());
        }
    }
    @Test
    void addIngredient(){
        Member creatorMember = memberRepository.findById(5).get();
        Ingredient newIngredient = ingredientRepository.save(
                Ingredient.builder()
                .ingredientName("모닝빵")
                .creator(creatorMember)
                .inDate(LocalDateTime.now())
                .build());
        System.out.println(newIngredient.getIngredientName());
    }
    @Test
    void addGroupBuyPeriod(){
        GroupBuy newGroupBuy = groupBuyRepository.save(
                GroupBuy.builder().in.build()
        )
    }
}
