package com.oopsw.matna.service;

import com.oopsw.matna.dto.MemberProfileListResponse;
import com.oopsw.matna.repository.GroupBuyParticipantRepository;
import com.oopsw.matna.repository.GroupBuyRepository;
import com.oopsw.matna.repository.MemberRepository;
import com.oopsw.matna.repository.ReportRepository;
import com.oopsw.matna.repository.entity.GroupBuy;
import com.oopsw.matna.repository.entity.GroupBuyParticipant;
import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.vo.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
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

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReportRepository reportRepository;

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
    @Transactional
    public void editPaymentTest() {

        int targetGroupBuyNo = 11;
        String dateStr = "2025-11-11 15:30:00";
        String description = "테스트.";


        MockMultipartFile mockFile = new MockMultipartFile(
                "receiptImage",
                "test_receipt.jpg",
                "image/jpeg",
                "fake image content".getBytes(StandardCharsets.UTF_8)
        );


        mypageService.addPayment(targetGroupBuyNo, mockFile, dateStr, description);


        GroupBuy result = groupBuyRepository.findById(targetGroupBuyNo).orElse(null);


        if (result != null) {
            System.out.println("========================================");
            System.out.println("결제 상태: " + result.getStatus());
            System.out.println("구매 날짜: " + result.getBuyDate());
            System.out.println("저장된 이미지 경로: " + result.getReceiptImageUrl());
            System.out.println("결제 메모: " + result.getPaymentNote());
            System.out.println("========================================");
        } else {
            System.out.println("테스트 실패: 해당 공구가 없습니다.");
        }

    }

    @Test
    public void removeMemberTest() {

        Integer memberNo = 20;

        mypageService.removeMember(memberNo);

        Member result = memberRepository.findById(memberNo).orElse(null);

        System.out.println(result.getMemberNo());
        System.out.println(result.getDelDate());
    }

    @Test
    public void checkPasswordTest() {

        Integer memberNo = 15;
        String realPassword = "member_11";
        String wrongPassword = "wrong_password";

        boolean isMatched = mypageService.checkPassword(memberNo, realPassword);
        System.out.println("어 형이야" + isMatched);

        boolean isNotMatched = mypageService.checkPassword(memberNo, wrongPassword);
        System.out.println("응 아니야" + isNotMatched);
    }

    @Test
    public void getMemberInfoTest() {

        Integer memberNo = 5;

        MemberVO member = mypageService.getMemberInfo(memberNo);

        System.out.println("ID: " + member.getMemberId());
        System.out.println("이름: " + member.getAccountName());
        System.out.println("닉네임: " + member.getNickname());
        System.out.println("주소: " + member.getAddress());
        System.out.println("전체 객체: " + member);
    }

//    @Test
//    @Transactional
//    public void editMemberInfoTest() {
//
//        int targetMemberNo = 5;
//
//        MemberVO editMember = MemberVO.builder()
//                .memberNo(targetMemberNo)
//                .nickname("수정했단말이오")
//                .password("new_password_123")
//                .imageUrl("enwene.jpg")
//                .bank("카카오뱅크")
//                .accountNumber("3333-11-2222")
//                .accountName("김루이지")
//                .address("경기도 성남시 판교 옥탑방왕세자")
//                .build();
//
//        mypageService.updateMemberProfile(editMember, 'dd.jpg');
//
//        Member result = memberRepository.findById(targetMemberNo).orElse(null);
//
//        System.out.println("변경된 닉네임: " + result.getNickname());
//        System.out.println("변경된 주소: " + result.getAddress());
//    }

    @Test
    @Transactional
    public void refundPointTest() {
        // Given
        int memberNo = 5;
        int currentPoint = 1000;


        int remain = mypageService.refundPoint(memberNo, 500);
        System.out.println("남은 돈: " + remain);


        try {
            mypageService.refundPoint(memberNo, 15000);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    @Transactional
    void reportMemberTest() {
        AllReportVO vo = AllReportVO.builder()
                .reporterNo(5)
                .targetMemberNo(6)
                .reason("회원 신고 테스트")
                .imageUrl("ct.jpg")
                .build();

        System.out.println("입력 데이터: " + vo);

        try {
            mypageService.addReportMember(vo);

            System.out.println("성공!");
        } catch (Exception e) {
            System.out.println("실패! " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    @Transactional
    void reportGroupTest() {
        int reporterNo = 5;
        int targetGroupNo = 11;

        AllReportVO vo = AllReportVO.builder()
                .reporterNo(reporterNo)
                .groupBuyNo(targetGroupNo)
                .reason("상품 상태가 다릅니다")
                .imageUrl("ctest.jpg")
                .build();

        System.out.println("입력 데이터: " + vo);

        try {
            mypageService.addReportGroupBuy(vo);

            System.out.println("성공!");
            System.out.println("DB 확인");

        } catch (Exception e) {
            System.out.println("실패! " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    @Transactional
    public void chargePointTest() {

        int memberNo = 5;
        Member member = memberRepository.findById(memberNo).get();
        int beforePoint = member.getPoint();
        int chargeAmount = 10000;


        int afterPoint = mypageService.chargePoint(memberNo, chargeAmount);


        System.out.println("충전 전: " + beforePoint);
        System.out.println("충전 후: " + afterPoint);

    }

    @Test
    @Transactional
    public void getGroupBuyParticipateListTest(){

        int memberNo = 9;
        String filterStatus = "";
        List<GroupBuyListVO> list = mypageService.getParticipatedGroupBuyList(memberNo, filterStatus);

        System.out.println(list);


    }

    @Test
    @Transactional
    public void getGroupBuyPeroidListTest(){

        int memberNo = 9;
        String filterStatus = "";
        List<GroupBuyListVO> list = mypageService.getHostedGroupBuyList(memberNo, filterStatus);

        System.out.println(list);


    }

    @Test
    @Transactional
    public void addArrivalTest() {

        int targetGroupBuyNo = 11;
        String dateString = "2025-11-12 14:00:00";


        MultipartFile testFile = null;


        mypageService.addArrival(targetGroupBuyNo, testFile, dateString);

        GroupBuy result = groupBuyRepository.findById(targetGroupBuyNo).orElse(null);


        System.out.println(result.getStatus());
    }




}

