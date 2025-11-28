package com.oopsw.matna.service;

import com.oopsw.matna.repository.IngredientRepository;
import com.oopsw.matna.repository.MemberRepository;
import com.oopsw.matna.repository.PeriodGroupBuyRepository;
import com.oopsw.matna.repository.entity.GroupBuy;
import com.oopsw.matna.repository.entity.Ingredient;
import com.oopsw.matna.repository.entity.PeriodGroupBuy;
import com.oopsw.matna.vo.PeroidGroupBuyCreateVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@Transactional
public class PeriodGroupBuyServiceTests {
    @Autowired
    private PeriodGroupBuyService periodGroupBuyService;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PeriodGroupBuyRepository periodGroupBuyRepository;

    @Test
    void getIngredientKeywordTest() {
        String keyword = "쌀";
        List<Ingredient> results = periodGroupBuyService.getIngredientKeyword(keyword);
        results.forEach(ingredient -> {
            System.out.println(ingredient.getIngredientName());
        });
    }

    @Test
    void addIngredientTest() {
        Integer creatorNo = 5;
        String ingredientName = "브로콜리";
        // 회원 존재 여부 확인
        memberRepository.findById(creatorNo)
                .orElseThrow(() -> new AssertionError("테스트용 회원(memberNo: " + creatorNo + ")이 존재하지 않습니다."));
        // 중복 재료명 확인
        List<Ingredient> existingIngredients = ingredientRepository.findByIngredientNameContaining(ingredientName);
        if (!existingIngredients.isEmpty()) {
            System.out.println("이미 존재하는 재료명입니다: " + ingredientName);
            return;
        }
        Ingredient savedIngredient = periodGroupBuyService.addIngredient(creatorNo, ingredientName);

        assertNotNull(savedIngredient);
        assertNotNull(savedIngredient.getIngredientNo());
        assertEquals(ingredientName, savedIngredient.getIngredientName());
        assertEquals(creatorNo, savedIngredient.getCreator().getMemberNo());
        assertNotNull(savedIngredient.getInDate());

        System.out.println("저장된 재료: " + savedIngredient.getIngredientName());
        System.out.println("생성자 번호: " + savedIngredient.getCreator().getMemberNo());
    }

    @Test
    void addPeriodGroupBuyTest() {
        Integer ingredientNo = 23;
        Integer creatorNo = 16;
        // 재료 존재 여부 확인
        ingredientRepository.findById(ingredientNo)
                .orElseThrow(() -> new AssertionError("테스트용 재료(ingredientNo: " + ingredientNo + ")가 존재하지 않습니다."));
        // 회원 존재 여부 확인
        memberRepository.findById(creatorNo)
                .orElseThrow(() -> new AssertionError("테스트용 회원(memberNo: " + creatorNo + ")이 존재하지 않습니다."));

        PeroidGroupBuyCreateVO vo = PeroidGroupBuyCreateVO.builder()
                .ingredientNo(ingredientNo)
                .creatorNo(creatorNo)
                .title("김치/동치미랑 먹으면 딱좋은 밤고구마 20kg 같이 사요")
                .buyEndDate(3)
                .shareEndDate(4)
                .shareTime(LocalTime.parse("17:00"))
                .shareLocation("서울 금천구 가산디지털1로 70")
                .shareDetailAddress("호서대벤처 1층입구")
                .price(65000)
                .quantity(20000)
                .unit("g")
                .feeRate(3)
                .imageUrl("http://example.com/image_path/goguma.jpg")
                .content("유기농 밤고구마예요~ 저렴한데 양이 너무 많아요")
                .itemSaleUrl("http://sale.site/item/123")
                .dueDate(LocalDateTime.of(2025, 11, 30, 17, 30))
                .maxParticipants(10)
                .build();

        PeriodGroupBuy result = periodGroupBuyService.addPeriodGroupBuy(vo);

        assertNotNull(result);
        assertNotNull(result.getPeriodGroupBuyNo());
        assertNotNull(result.getGroupBuy());

        // GroupBuy 검증
        GroupBuy groupBuy = result.getGroupBuy();
        assertEquals("김치/동치미랑 먹으면 딱좋은 밤고구마 20kg 같이 사요", groupBuy.getTitle());
        assertEquals(ingredientNo, groupBuy.getIngredient().getIngredientNo());
        assertEquals(creatorNo, groupBuy.getCreator().getMemberNo());
        assertEquals(65000, groupBuy.getPrice());
        assertEquals(20000, groupBuy.getQuantity());
        assertEquals("g", groupBuy.getUnit());
        assertEquals(3, groupBuy.getFeeRate());
        assertEquals("open", groupBuy.getStatus());
        assertEquals(0, groupBuy.getScrapCount());
        assertEquals("서울 금천구 가산디지털1로 70", groupBuy.getShareLocation());
        assertEquals("호서대벤처 1층입구", groupBuy.getShareDetailAddress());
        // PeriodGroupBuy 검증
        assertEquals(LocalDateTime.of(2025, 11, 30, 17, 30), result.getDueDate());
        assertEquals(10, result.getMaxParticipants());

        // DB 조회 확인
        PeriodGroupBuy savedPeriodGroupBuy = periodGroupBuyRepository.findById(result.getPeriodGroupBuyNo())
                .orElseThrow(() -> new AssertionError("저장된 기간 공동구매를 찾을 수 없습니다."));

        assertEquals(result.getPeriodGroupBuyNo(), savedPeriodGroupBuy.getPeriodGroupBuyNo());

        System.out.println("GroupBuy 제목: " + groupBuy.getTitle());
        System.out.println("PeriodGroupBuy 마감일: " + result.getDueDate());
        System.out.println("최대 참여자 수: " + result.getMaxParticipants());
    }

    @Test
    void addParticipantToPeriodGroupBuyTest(){

    }
}
