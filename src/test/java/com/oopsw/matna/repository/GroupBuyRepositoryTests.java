package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
public class GroupBuyRepositoryTests {
    @Autowired
    IngredientRepository ingredientRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    GroupBuyRepository groupBuyRepository;
    @Autowired
    PeroidGroupBuyRepository peroidGroupBuyRepository;
    @Autowired
    GroupBuyParticipantRepository groupBuyParticipantRepository;

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
    void addPeriodGroupBuy(){
        Ingredient ingredientNo = ingredientRepository.findById(23).get();
        Member creatorMember = memberRepository.findById(16).get();
        GroupBuy newGroupBuy = groupBuyRepository.save(
                GroupBuy.builder()
                .ingredientNo(ingredientNo) //밤고구마
                .creatorNo(creatorMember)
                .title("김치/동치미랑 먹으면 딱좋은 밤고구마 20kg 같이 사요")
                .buyEndDate(3)
                .shareEndDate(4)
                .shareTime(LocalTime.parse("17:00")) //시간 데이터만
                .shareLocation("서울 금천구 가산디지털1로 70")
                .shareDetailAddress("호서대벤처 1층입구")
                .price(65000)
                .quantity(20000)
                .unit("g")
                .feeRate(3)
                .imageUrl("http://example.com/image_path/goguma.jpg")
                .content("유기농 밤고구마예요~ 저렴한데 양이 너무 많아요")
                .inDate(LocalDateTime.now())
                .itemSaleUrl("http://sale.site/item/123")
                .scrapCount(0)
                .status("open")
                .build()
        );
        PeriodGroupBuy newPeriodGroupBuy = peroidGroupBuyRepository.save(
                PeriodGroupBuy.builder()
                .groupBuyNo(newGroupBuy) // 부모 GroupBuy 엔티티 연결 (FK 설정)
                .dueDate(LocalDateTime.of(2025,11,30,17,30))
                .maxParticipants(10)
                .build()
        );
        System.out.println(newGroupBuy.getTitle());
        System.out.println(newPeriodGroupBuy.getDueDate());
    }

    @Test
    void joinPeriodGroupBuy(){
        Member participantMember = memberRepository.findById(12).get();
        GroupBuy groupBuyNo = groupBuyRepository.findById(29).get();

        Integer price = groupBuyNo.getPrice();
        Integer feeRate = groupBuyNo.getFeeRate();
        int initialPaymentPoint = (int) Math.round((price * (1.0 + (feeRate / 100.0))) / 2.0);

        GroupBuyParticipant joinPeriodGroupBuy = groupBuyParticipantRepository.save(
                GroupBuyParticipant.builder()
                        .participantNo(participantMember)
                        .groupBuyNo(groupBuyNo)
                        .participatedDate(LocalDateTime.now())
                        .initialPaymentPoint(initialPaymentPoint)
                        .build()
        );
        System.out.println(joinPeriodGroupBuy.getInitialPaymentPoint());
    }

    @Test
    void payPeroidPoint(){
        Member participantMember = memberRepository.findById(12).get();
        GroupBuyParticipant groupBuyParticipant = groupBuyParticipantRepository.findById(67).get();

        int initialPaymentPoint = groupBuyParticipant.getInitialPaymentPoint(); // 차감할 포인트
        int currentPoint = participantMember.getPoint();
        int newPoint = currentPoint - initialPaymentPoint;
        if (newPoint < 0) {
            return; // 테스트 중단
        }

        participantMember.setPoint(newPoint);
        Member payPeroidMember = memberRepository.save(participantMember);

        System.out.println(initialPaymentPoint+" "+currentPoint+" "+newPoint);
    }

    @Test
    void cancelJoinGroupBuy(){
        Member participantMember = memberRepository.findById(12).get();
        GroupBuyParticipant groupBuyParticipant = groupBuyParticipantRepository.findById(67).get();

        groupBuyParticipant.setCancelDate(LocalDateTime.now());
        groupBuyParticipantRepository.save(groupBuyParticipant);

        int initialPaymentPoint = groupBuyParticipant.getInitialPaymentPoint(); //지불한돈
        int currentPoint = participantMember.getPoint();
        int newPoint = currentPoint + initialPaymentPoint; // 현재 포인트 + 지불했던 포인트
        participantMember.setPoint(newPoint);
        memberRepository.save(participantMember);

        System.out.println(initialPaymentPoint+" "+currentPoint+" "+newPoint);
    }


}
