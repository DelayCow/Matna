package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Ingredient;
import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.vo.IngredientVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class IngredientRepositoryTest {

    @Autowired
    IngredientRepository ingredientRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void SearchIngredientTest() {

        String keyword = "감자";

        List<Ingredient> entities = ingredientRepository.findByIngredientNameContainingAndDelDateIsNull(keyword);


        List<IngredientVO> voList = new ArrayList<>();

        for (Ingredient entity : entities) {
            IngredientVO vo = new IngredientVO(
                    entity.getIngredientNo(),
                    entity.getIngredientName()
            );
            voList.add(vo);
        }

            System.out.println(keyword + " 검색 결과");
            for (IngredientVO vo : voList) {
                System.out.println(vo.getIngredientNo() + " 번 재료 " + vo.getIngredientName());
            }

        }

        @Test
        void insertNewIngredientTest() {
            String newIngredientName = "테스트재료";
            Integer memberId = 1;

        // 중복 검사
        if (ingredientRepository.existsByIngredientName(newIngredientName)) {
            System.out.println("이미 존재하는 재료입니다.");
        }


        Member creator = memberRepository.findById(memberId)
                .get();


        Ingredient newIngredient = Ingredient.builder()
                .ingredientName(newIngredientName)
                .creator(creator)
                .inDate(LocalDateTime.now())
                .build();


        Ingredient savedItem = ingredientRepository.save(newIngredient);



        System.out.println("재료 등록 성공");
        System.out.println("번호: " + savedItem.getIngredientNo());
        System.out.println("재료명: " + savedItem.getIngredientName());
        System.out.println("작성자: " + savedItem.getCreator().getNickname());
    }


    }
