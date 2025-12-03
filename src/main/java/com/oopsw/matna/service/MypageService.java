package com.oopsw.matna.service;

import com.oopsw.matna.dao.GroupBuyListDAO;
import com.oopsw.matna.dto.MemberProfileListResponse;
import com.oopsw.matna.repository.*;
import com.oopsw.matna.repository.entity.*;
import com.oopsw.matna.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final RecipeRepository recipeRepository;

    private final MemberRepository memberRepository;

    private final ReviewsRepository reviewsRepository;

    private final GroupBuyParticipantRepository groupBuyParticipantRepository;

    private final GroupBuyRepository groupBuyRepository;

    private final ReportRepository reportRepository;

    private final MemberReportRepository memberReportRepository;

    private final GroupBuyReportRepository groupBuyReportRepository;

    private final GroupBuyListDAO groupBuyListDAO;
    private final PeriodGroupBuyRepository periodGroupBuyRepository;


    public List<RecipeVO> getMypageRecipeList(Integer memberNo) {


        List<Recipe> recipes = recipeRepository.findByAuthor_MemberNoAndDelDateIsNull(memberNo);
        return recipes.stream()
                .map(recipe -> RecipeVO.builder()
                        .recipeNo(recipe.getRecipeNo())
                        .title(recipe.getTitle())
                        .averageRating(recipe.getAverageRating())
                        .reviewCount(recipe.getReviewCount())
                        .thumbnailUrl(recipe.getImageUrl())
                        .difficulty(recipe.getDifficulty())
                        .prepTime(recipe.getPrepTime())
                        .servings(recipe.getServings())
                        .spicyLevel(recipe.getSpicyLevel())
                        .build()).collect(Collectors.toList());
    }

    public MemberProfileListResponse getMypageMember(Integer memberNo) {

        Member m = memberRepository.findById(memberNo).get();

        return MemberProfileListResponse.builder()
                .nickname(m.getNickname())
                .imageUrl(m.getImageUrl())
                .points(m.getPoint())
                .build();

    }

    public void removeMypageRecipe(Integer recipeNo) {
        Recipe recipe = recipeRepository.findById(recipeNo).get();

        recipe.setDelDate(LocalDateTime.now());
        recipeRepository.save(recipe);

    }

    public List<ReviewsListVO> getMypageReviewsList(int memberNo) {

        List<Reviews> entities = reviewsRepository.findReviewsByAuthor_MemberNoAndDelDateIsNullOrderByInDateDesc(memberNo);

        return entities.stream()
                .map(r -> ReviewsListVO.builder()
                        .reviewNo(r.getReviewNo())
                        .title(r.getTitle())
                        .imageUrl(r.getImageUrl())
                        .rating(r.getRating())
                        .inDate(r.getInDate())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ReviewsListVO> removeReviews(Integer reviewsNo){

        Reviews review = reviewsRepository.findById(reviewsNo).get();
        Recipe recipe = review.getRecipe();

        int recipeReviewCount = recipe.getReviewCount();
        float recipeAverageRating = recipe.getAverageRating();

        review.setDelDate(LocalDateTime.now());
        reviewsRepository.save(review);

        recipe.removeRating(review.getRating());
        recipe.setUpdateDate(LocalDateTime.now());
        recipeRepository.save(recipe);


        return null;
    }


    public void editShareGroupBuy(GroupBuyParticipantVO sharedData) {


        LocalDateTime receiveDate = sharedData.getReceiveDate();


        GroupBuyParticipant participant = groupBuyParticipantRepository
                .findByGroupBuy_GroupBuyNoAndParticipant_MemberNo(
                        sharedData.getGroupBuyNo(),
                        sharedData.getParticipantNo()
                );

        if (participant == null) {
            throw new RuntimeException("참여 정보를 찾을 수 없습니다.");
        }

        participant.setReceiveDate(receiveDate);
        groupBuyParticipantRepository.save(participant);
    }

    public void editPayment(GroupBuyVO paymentData) {


        GroupBuy groupBuy = groupBuyRepository.findById(paymentData.getGroupBuyNo())
                .get();

        groupBuy.setStatus("paid");
        groupBuy.setReceiptImageUrl(paymentData.getReceiptImageUrl());
        groupBuy.setBuyDate(paymentData.getBuyDate());
        groupBuy.setPaymentNote(paymentData.getPaymentNote());

        groupBuyRepository.save(groupBuy);
    }


    public void addArrival(GroupBuyVO deliveryData) {


        GroupBuy groupBuy = groupBuyRepository.findById(deliveryData.getGroupBuyNo())
                .get();

        groupBuy.setStatus("delivered");

        groupBuy.setArrivalImageUrl(deliveryData.getArrivalImageUrl());
        groupBuy.setArrivalDate(deliveryData.getArrivalDate());

        groupBuyRepository.save(groupBuy);
    }

    public LocalDateTime removeMember(Integer memberNo) {

        Member member = memberRepository.findById(memberNo)
                .get();

        LocalDateTime now = LocalDateTime.now();
        member.setDelDate(now);

        memberRepository.save(member);

        return now;
    }

    public boolean checkPassword(Integer memberNo, String inputPassword) {

        Member member = memberRepository.findById(memberNo)
                .get();

        return member.getPassword().equals(inputPassword);
    }

    public MemberVO getMemberInfo(Integer memberNo) {

        Member m = memberRepository.findById(memberNo)
                .get();

        return MemberVO.builder()
                .memberNo(m.getMemberNo())
                .memberId(m.getMemberId())
                // .password(m.getPassword())
                .accountName(m.getAccountName())
                .nickname(m.getNickname())
                .bank(m.getBank())
                .accountNumber(m.getAccountNumber())
                .inDate(m.getInDate())
                .delDate(m.getDelDate())
                .roll(m.getRoll())
                .banDate(m.getBanDate())
                .point(m.getPoint())
                .imageUrl(m.getImageUrl())
                .address(m.getAddress())
                .build();
    }


    public void updateMemberProfile(MemberVO editData) {

        Member member = memberRepository.findById(editData.getMemberNo())
                        .get();

        member.setNickname(editData.getNickname());
        member.setImageUrl(editData.getImageUrl());
        member.setAddress(editData.getAddress());


        member.setBank(editData.getBank());
        member.setAccountNumber(editData.getAccountNumber());
        member.setAccountName(editData.getAccountName());


        if (editData.getPassword() != null && !editData.getPassword().isEmpty()) {

            member.setPassword(editData.getPassword());
        }

    }


    public int refundPoint(Integer memberNo, int refundAmount) {


        Member member = memberRepository.findById(memberNo)
                .get();


        if (member.getPoint() < refundAmount) {
            throw new RuntimeException("보유 포인트보다 환급 금액이 클 수 없습니다.");
        }

        int newPoint = member.getPoint() - refundAmount;
        member.setPoint(newPoint);

        return newPoint;
    }

    public void addReportMember(AllReportVO vo) {


        Member reporter = memberRepository.findById(vo.getReporterNo())
                .get();

        Report report = Report.builder()
                .reporter(reporter)
                .imageUrl(vo.getImageUrl())
                .reason(vo.getReason())
                .status("WIP")
                .reportedDate(LocalDateTime.now())
                .build();

        Report savedReport = reportRepository.save(report);

        Member target = memberRepository.findById(vo.getTargetMemberNo())
                .orElseThrow(() -> new RuntimeException("신고 대상 회원이 없습니다."));

        memberReportRepository.save(MemberReport.builder()
                .report(savedReport)
                .targetMember(target)
                .build());
    }

    public void addReportGroupBuy(AllReportVO vo) {


        Member reporter = memberRepository.findById(vo.getReporterNo())
                .orElseThrow(() -> new RuntimeException("신고자 정보가 없습니다."));

        Report report = Report.builder()
                .reporter(reporter)
                .imageUrl(vo.getImageUrl())
                .reason(vo.getReason())
                .status("WIP")
                .reportedDate(LocalDateTime.now())
                .build();

        Report savedReport = reportRepository.save(report);

        GroupBuy target = groupBuyRepository.findById(vo.getGroupBuyNo())
                .orElseThrow(() -> new RuntimeException("신고 대상 공구가 없습니다."));

        groupBuyReportRepository.save(GroupBuyReport.builder()
                .report(savedReport)
                .groupBuy(target)
                .build());
    }

    @Transactional
    public int chargePoint(Integer memberNo, int amount) {


        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        if (amount <= 1000) {
            throw new RuntimeException("최소 충전 금액은 1000원 입니다.");
        }


        int newPoint = member.getPoint() + amount;
        member.setPoint(newPoint);


        return newPoint;
    }

    public List<GroupBuyListVO> getParticipatedGroupBuyList(Integer memberNo, String filterStatus) {
        if (filterStatus == null || filterStatus.isEmpty()) {
            filterStatus = "ALL";
        }
        return groupBuyListDAO.getParticipantList(memberNo, filterStatus);
    }



}
