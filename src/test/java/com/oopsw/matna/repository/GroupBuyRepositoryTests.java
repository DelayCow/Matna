package com.oopsw.matna.Repository;

import com.oopsw.matna.repository.GroupBuyRepository;
import com.oopsw.matna.repository.IngredientRepository;
import com.oopsw.matna.repository.entity.Ingredient;
import com.oopsw.matna.repository.entity.GroupBuy;
import com.oopsw.matna.vo.GroupBuyListVO;
import com.oopsw.matna.vo.GroupBuyVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class GroupBuyRepositoryTests {
    @Autowired
    IngredientRepository ingredientRepository;
    @Autowired
    GroupBuyRepository groupBuyRepository;

    @Test
    void searchIngredientKeyword(){
        String keyword = "쌀";
        List<Ingredient> results = ingredientRepository.findByIngredientNameContaining(keyword);
        for (Ingredient ingredient : results) {
            System.out.println(ingredient.getIngredientName());
            }
    };

    @Test
    public void getMyPageGroupBuyListTest(){
        Integer memberNo = 5;
        List<GroupBuy> groupBuys = groupBuyRepository.findByCreator_MemberNo(memberNo);
        List<GroupBuyListVO> groupBuyList = groupBuys.stream().map(groupBuy -> GroupBuyListVO.builder()
                .groupBuyNo(groupBuy.getGroupBuyNo())
                .title(groupBuy.getTitle())
                .imageUrl(groupBuy.getImageUrl())
                .status(groupBuy.getStatus()).build()).collect(Collectors.toList());
        System.out.println(groupBuyList);
    };
    @Test
    public void addPaymentDataTest(){
        //response에서는 string으로 받고 controller에서 넘겨줄때 타입 바꿔서 넘겨주기
        String dateStringWithTime = "2025-11-11 15:30:00";//브라우저에서 넘어올때 초도 0으로 채워서 보내기
        DateTimeFormatter formatterWithTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime buyDate = LocalDateTime.parse(dateStringWithTime, formatterWithTime);
        GroupBuyVO paymentData = GroupBuyVO.builder()
                .groupBuyNo(11)
                .receiptImageUrl("receipt.jpg")
                .buyDate(buyDate)
                .paymentNote("테스트2")
                .build();
        GroupBuy groupBuy = groupBuyRepository.findById(paymentData.getGroupBuyNo()).get();
        groupBuy.setStatus("paid");
        groupBuy.setReceiptImageUrl(paymentData.getReceiptImageUrl());
        groupBuy.setBuyDate(paymentData.getBuyDate());
        groupBuy.setPaymentNote(paymentData.getPaymentNote());
        groupBuyRepository.save(groupBuy);
    };

    @Test
    public void addDelivaryDataTest(){
        //response에서는 string으로 받고 controller에서 넘겨줄때 타입 바꿔서 넘겨주기
        String dateStringWithTime = "2025-11-12 00:00:00";
        DateTimeFormatter formatterWithTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime arrivalDate = LocalDateTime.parse(dateStringWithTime, formatterWithTime);
        GroupBuyVO deliveryData = GroupBuyVO.builder()
                .groupBuyNo(11)
                .arrivalImageUrl("arrival.jpg")
                .arrivalDate(arrivalDate)
                .build();
        GroupBuy groupBuy = groupBuyRepository.findById(deliveryData.getGroupBuyNo()).get();
        groupBuy.setStatus("delivered");
        groupBuy.setArrivalImageUrl(deliveryData.getArrivalImageUrl());
        groupBuy.setArrivalDate(deliveryData.getArrivalDate());
        groupBuyRepository.save(groupBuy);
    };
};
