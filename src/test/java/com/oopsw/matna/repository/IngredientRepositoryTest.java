package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Ingredient;
import com.oopsw.matna.vo.IngredientVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class IngredientRepositoryTest {

    @Autowired
    IngredientRepository ingredientRepository;

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

    }
