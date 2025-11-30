package com.oopsw.matna.service;

import com.oopsw.matna.dto.MemberProfileListResponse;
import com.oopsw.matna.repository.GroupBuyParticipantRepository;
import com.oopsw.matna.repository.GroupBuyRepository;
import com.oopsw.matna.repository.entity.GroupBuy;
import com.oopsw.matna.repository.entity.GroupBuyParticipant;
import com.oopsw.matna.vo.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
public class MypageServiceTests {
    @Autowired
    private MypageService mypageService;

    @Autowired
    private GroupBuyParticipantRepository groupBuyParticipantRepository;

    @Autowired
    private GroupBuyRepository groupBuyRepository;

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

    @Test
    public void removeMypageRecipeTest() {

        mypageService.removeMypageRecipe(13);


    }

    @Test
    public void editShareGroupBuyTest() {


        String inputDateStr = "2025-12-25 10:00:00";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime receiveDate = LocalDateTime.parse(inputDateStr, formatter);

        GroupBuyParticipantVO requestVO = GroupBuyParticipantVO.builder()
                .groupBuyNo(11)
                .participantNo(15)
                .receiveDate(receiveDate)
                .build();


        mypageService.editShareGroupBuy(requestVO);


        GroupBuyParticipant result = groupBuyParticipantRepository
                .findByGroupBuy_GroupBuyNoAndParticipant_MemberNo(11, 15);

        System.out.println(result.getReceiveDate());

    }

    @Test
    public void editPaymentTest() {


        int targetGroupBuyNo = 11;


        String dateStr = "2025-11-11 15:30:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime buyDate = LocalDateTime.parse(dateStr, formatter);

        GroupBuyVO paymentData = GroupBuyVO.builder()
                .groupBuyNo(targetGroupBuyNo)
                .receiptImageUrl("image.jpg") // 가짜 이미지 경로
                .buyDate(buyDate)
                .paymentNote("테스트 결제 메모입니다.")
                .build();

        mypageService.editPayment(paymentData);

        GroupBuy result = groupBuyRepository.findById(targetGroupBuyNo).orElse(null);

        System.out.println(result.getBuyDate());

    }

    @Test
    public void addDeliveryDataTest() {

        int targetGroupBuyNo = 11;

        String dateString = "2025-11-12 14:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime arrivalDate = LocalDateTime.parse(dateString, formatter);

        GroupBuyVO deliveryData = GroupBuyVO.builder()
                .groupBuyNo(targetGroupBuyNo)
                .arrivalImageUrl("arrival_test.jpg")
                .arrivalDate(arrivalDate)
                .build();


        mypageService.addArrival(deliveryData);


        GroupBuy result = groupBuyRepository.findById(targetGroupBuyNo).orElse(null);


        System.out.println(result.getStatus());
        System.out.println(result.getArrivalDate());
    }
}

