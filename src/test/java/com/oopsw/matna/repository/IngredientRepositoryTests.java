package com.oopsw.matna.repository;

import com.oopsw.matna.repository.IngredientRepository;
import com.oopsw.matna.repository.MemberRepository;
import com.oopsw.matna.repository.entity.GroupBuy;
import com.oopsw.matna.repository.entity.Ingredient;
import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.vo.IngredientVO;
import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.repository.entity.RecipeIngredient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class IngredientRepositoryTests {
    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private GroupBuyRepository groupBuyRepository;

    @Autowired
    MemberRepository memberRepository;
    private RecipeIngredientRepository recipeIngredientRepository;

    @Transactional //lazy loading 해결하기 위해 넣음.
    @Test
    public void SearchIngredientTest() {

        String keyword = "감자";

        List<Ingredient> entities = ingredientRepository.findByIngredientNameContainingAndDelDateIsNull(keyword);


        List<IngredientVO> voList = new ArrayList<>();

        for (Ingredient entity : entities) {
            IngredientVO vo = IngredientVO.builder()
                    .ingredientNo(entity.getIngredientNo())
                    .ingredientName(entity.getIngredientName()).build();
            voList.add(vo);
        }

            System.out.println(keyword + " 검색 결과");
            for (IngredientVO vo : voList) {
                System.out.println(vo.getIngredientNo() + " 번 재료 " + vo.getIngredientName());
            }

    }

    @Test
    public void insertNewIngredientTest() {
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

    @Test
    public void findAll() {
        System.out.println(ingredientRepository.findAll());
    }

    @Transactional
    @Test
    @Commit
    public void updateDelDate() {
        Ingredient ingredient = ingredientRepository.findById(50)
                .orElseThrow(() -> new RuntimeException("재료가 존재하지 않습니다."));
        ingredient.setDelDate(LocalDateTime.now());
        // @Transactional 안에서 수정했으므로 save() 호출 없이 DB 반영
    }

    @Transactional
    @Test
    public void findAllByApproveDateIsNull() {
        System.out.println(ingredientRepository.findAllByApproveDateIsNull());
    }

    @Transactional
    @Test
    @Commit
    public void approve() {
        Ingredient ingredient = ingredientRepository.findById(46)
                .orElseThrow(() -> new RuntimeException("재료가 존재하지 않습니다."));

        ingredient.setApproveDate(LocalDateTime.now());
    }

    @Test
    @Transactional
    @Commit
    public void changeIngredientTest(){
        Integer ingredientNo = 51;
        Integer newIngredientNo = 48;
        Ingredient ingredient = ingredientRepository.findById(ingredientNo).get();
        Ingredient newIngredient = ingredientRepository.findById(newIngredientNo).get();
        List<GroupBuy> groupBuyList = groupBuyRepository.findByIngredient_IngredientNo(ingredientNo);
        List<RecipeIngredient> recipeList = recipeIngredientRepository.findByIngredient_IngredientNo(ingredientNo);
        groupBuyList.forEach(groupBuy -> {groupBuy.setIngredient(newIngredient);});
        recipeList.forEach(recipe -> {recipe.setIngredient(newIngredient);});
        ingredient.setDelDate(LocalDateTime.now());
    }


}
