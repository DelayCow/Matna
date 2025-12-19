package com.oopsw.matna.service;

import com.oopsw.matna.dao.GroupBuyListDAO;
import com.oopsw.matna.dto.MemberProfileListResponse;
import com.oopsw.matna.repository.*;
import com.oopsw.matna.repository.entity.*;
import com.oopsw.matna.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
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

    private final ImageStorageService imageStorageService;
    private final PasswordEncoder passwordEncoder;


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
                        .spicyLevel(r.getSpicyLevel())
                        .build())
                .collect(Collectors.toList());
    }


    public void editShareGroupBuy(GroupBuyParticipantVO sharedData) {

        if (sharedData.getGroupParticipantNo() == null) {
            throw new RuntimeException("참여 번호(PK)가 전달되지 않았습니다.");
        }

        LocalDateTime receiveDate = sharedData.getReceiveDate();

        // 2. .get() 대신 orElseThrow 사용 (데이터가 없어도 안전하게 예외 처리)
        GroupBuyParticipant participant = groupBuyParticipantRepository
                .findById(sharedData.getGroupParticipantNo())
                .orElseThrow(() -> new RuntimeException("참여 정보를 찾을 수 없습니다."));

        participant.setReceiveDate(receiveDate);
        groupBuyParticipantRepository.save(participant);
    }

    @Transactional
    public void addPayment(int groupBuyNo, MultipartFile file, String buyDateStr, String description) {


        try {
            String savedFileUrl = null;
            if (file != null && !file.isEmpty()) {

                savedFileUrl = imageStorageService.save(file, "receipt");
            }


            String cleanDate = buyDateStr.replace("T", " ");
            if (cleanDate.length() == 16) cleanDate += ":00";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime buyDate = LocalDateTime.parse(cleanDate, formatter);


            GroupBuy groupBuy = groupBuyRepository.findById(groupBuyNo)
                    .orElseThrow(() -> new RuntimeException("공동구매 정보를 찾을 수 없습니다."));

            groupBuy.setStatus("PAID");


            groupBuy.setReceiptImageUrl(savedFileUrl);

            groupBuy.setBuyDate(buyDate);

            if (description != null) {
                groupBuy.setPaymentNote(description);
            }

            groupBuyRepository.save(groupBuy);

        } catch (Exception e) {
            throw new RuntimeException("결제 등록 중 오류 발생: " + e.getMessage());
        }

    }


    @Transactional
    public void addArrival(int groupBuyNo, MultipartFile file, String arrivalDateString) {

        try {

            String savedFileUrl = null;
            if (file != null && !file.isEmpty()) {
                savedFileUrl = imageStorageService.save(file, "arrival");
            }


            String cleanDate = arrivalDateString.replace("T", " ");
            if (cleanDate.length() == 16) cleanDate += ":00";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime arrivalDate = LocalDateTime.parse(cleanDate, formatter);


            GroupBuy groupBuy = groupBuyRepository.findById(groupBuyNo)
                    .orElseThrow(() -> new RuntimeException("공동구매 정보를 찾을 수 없습니다."));

            groupBuy.setArrivalImageUrl(savedFileUrl);
            groupBuy.setArrivalDate(arrivalDate);
            groupBuy.setStatus("DELIVERED");


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("도착 정보 등록 중 오류 발생: " + e.getMessage());
        }
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

        String dbPassword = member.getPassword(); // db 비번

        if (passwordEncoder.matches(inputPassword, dbPassword)) {
            return true;
        }
        return false;
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
                .role(m.getRole())
                .banDate(m.getBanDate())
                .point(m.getPoint())
                .imageUrl(m.getImageUrl())
                .address(m.getAddress())
                .build();
    }


    @Transactional
    public void updateMemberProfile(MemberVO editData, MultipartFile file) {

        Member member = memberRepository.findById(editData.getMemberNo())
                .get();


        member.setNickname(editData.getNickname());
        member.setAddress(editData.getAddress());
        member.setBank(editData.getBank());
        member.setAccountNumber(editData.getAccountNumber());
        member.setAccountName(editData.getAccountName());


        if (file != null && !file.isEmpty()) {
            try {

                String savedPath = imageStorageService.save(file, "profile");


                member.setImageUrl(savedPath);

            } catch (IOException e) {
                throw new RuntimeException("프로필 이미지 저장 중 오류 발생", e);
            }
        }


        if (editData.getPassword() != null && !editData.getPassword().isEmpty()) {
            String encodedPwd = passwordEncoder.encode(editData.getPassword());
            member.setPassword(encodedPwd);
        }

    }

    @Transactional
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

    @Transactional
    public void addReportMember(AllReportVO vo) throws IOException { // IOException 추가 필수


        MultipartFile file = vo.getImageFile();


        if (file != null && !file.isEmpty()) {
            String path = imageStorageService.save(file, "report");
            vo.setImageUrl(path);
        }


        Member reporter = memberRepository.findById(vo.getReporterNo()).get();

        Report report = Report.builder()
                .reporter(reporter)
                .imageUrl(vo.getImageUrl())
                .reason(vo.getReason())
                .status("WIP")
                .reportedDate(LocalDateTime.now())
                .build();

        Report savedReport = reportRepository.save(report);

        Member target = memberRepository.findById(vo.getTargetMemberNo())
                .orElseThrow(() -> new IllegalArgumentException("신고 대상 회원이 없습니다."));

        memberReportRepository.save(MemberReport.builder()
                .report(savedReport)
                .targetMember(target)
                .build());
    }


    public void addReportGroupBuy(AllReportVO vo) throws IOException {

        MultipartFile file = vo.getImageFile();

        if (file != null && !file.isEmpty()) {
            String path = imageStorageService.save(file, "report");
            vo.setImageUrl(path); // 저장된 경로를 VO에 셋팅!
        }

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

        if (amount < 1000) {
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


    public List<GroupBuyListVO> getHostedGroupBuyList(Integer memberNo, String filterStatus) {

        if (filterStatus == null || filterStatus.isEmpty()) {
            filterStatus = "ALL";
        }
        return groupBuyListDAO.getHostList(memberNo, filterStatus);
    }


}
