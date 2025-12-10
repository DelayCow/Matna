package com.oopsw.matna.service;

import com.oopsw.matna.dao.ManagerDAO;
import com.oopsw.matna.dao.ReportDAO;
import com.oopsw.matna.dto.ManagerGroupBuyResponse;
import com.oopsw.matna.dto.ManagerIngredientResponse;
import com.oopsw.matna.dto.ManagerMemberResponse;
import com.oopsw.matna.dto.ManagerReportResponse;
import com.oopsw.matna.repository.*;
import com.oopsw.matna.repository.entity.*;
import com.oopsw.matna.vo.AllGroupBuyListVO;
import com.oopsw.matna.vo.AllMemberListVO;
import com.oopsw.matna.vo.AllReportVO;
import com.oopsw.matna.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {
    private final IngredientRepository ingredientRepository;
    private final MemberRepository memberRepository;
    private final ManagerDAO managerDAO;
    private final ReportDAO reportDAO;
    private final ReportRepository reportRepository;
    private final GroupBuyRepository groupBuyRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    //재료 관리
    private ManagerIngredientResponse toManagerIngredientResponse(Ingredient ingredient) {
        return ManagerIngredientResponse.builder()
                .ingredientId(ingredient.getIngredientNo())       // 엔티티 필드명 확인
                .ingredientName(ingredient.getIngredientName())
                .creatorName(ingredient.getCreator().getNickname())
                .inDate(ingredient.getInDate())
                .approveDate(ingredient.getApproveDate())
                .build();
    }

    public List<ManagerIngredientResponse> getIngredients() {
        return ingredientRepository.findAllByApproveDateIsNotNullAndDelDateIsNullOrderByIngredientNoDesc()
                .stream()
                .map(this::toManagerIngredientResponse)
                .toList();
    }

    public List<ManagerIngredientResponse> getIngredientsByName(String keyword) {
        return ingredientRepository
                .findByIngredientNameContainingAndDelDateIsNull(keyword)
                .stream()
                .map(this::toManagerIngredientResponse)
                .toList();
    }

    public List<ManagerIngredientResponse> getNotApprovedIngredients() {
        return ingredientRepository
                .findAllByApproveDateIsNull()
                .stream()
                .map(this::toManagerIngredientResponse)
                .toList();
    }

    @Transactional
    public ManagerIngredientResponse addIngredient(Integer creatorId, String ingredientName) {
        // creator 정보 조회
        Member creator = memberRepository.findByMemberNo(creatorId);

        Ingredient ingredient = Ingredient.builder()
                .ingredientName(ingredientName)
                .creator(creator)
                .inDate(LocalDateTime.now())
                .approveDate(LocalDateTime.now())
                .build();

        Ingredient saved = ingredientRepository.save(ingredient);

        return toManagerIngredientResponse(saved);
    }

    @Transactional
    public void deleteIngredient(Integer ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("재료를 찾을 수 없습니다."));
        ingredient.setDelDate(LocalDateTime.now());
        ingredientRepository.save(ingredient);
    }

    @Transactional
    public void approveIngredient(Integer ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("재료를 찾을 수 없습니다."));
        ingredient.setApproveDate(LocalDateTime.now());
        ingredientRepository.save(ingredient);
    }

    @Transactional
    public void changeIngredient(Integer ingredientId, Integer newIngredientId) {
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

    //공구 관리
    private ManagerGroupBuyResponse toManagerGroupBuyResponse(AllGroupBuyListVO vo) {
        return ManagerGroupBuyResponse.builder()
                .groupBuyNo(vo.getGroupBuyNo())
                .status(vo.getStatus())
                .inDate(vo.getInDate())
                .creatorName(vo.getNickname())
                .title(vo.getTitle())
                .groupBuyCase(vo.getGroupBuyCase())
                .quantityGroupBuyNo(vo.getQuantityGroupBuyNo())
                .periodGroupBuyNo(vo.getPeriodGroupBuyNo())
                .build();
    }

    public List<ManagerGroupBuyResponse> getGroupBuyList(String startDate, String endDate, String title, String status){
        return managerDAO.getAllGroupBuyList(startDate, endDate, title, status)
                .stream()
                .map(this::toManagerGroupBuyResponse)
                .toList();
    }

    //신고 관리
    private ManagerReportResponse toManagerReportResponse(AllReportVO vo) {
        Member reporter = memberRepository.findById(vo.getReporterNo()).get();
        String type = (vo.getTargetNo() != null) ? "회원 신고" : "공동구매 신고";
        return ManagerReportResponse.builder()
                .managerReportId(vo.getReportNo())
                .status(vo.getStatus())
                .reportedDate(vo.getReportedDate())
                .reporterName(reporter.getNickname())
                .reason(vo.getReason())
                .type(type)
                .build();
    }

    public List<ManagerReportResponse> getReportList(LocalDate startDate, LocalDate endDate, String status, String reportCase, String keyword){
        return reportDAO.getReports(startDate, endDate, status, reportCase, keyword)
                .stream()
                .map(this::toManagerReportResponse)
                .toList();
    }

    public Report getReportById(Integer reportId) {
        return reportRepository.findWithReporterByReportNo(reportId)
                .orElseThrow(() -> new RuntimeException("Report가 없습니다."));
    }

    @Transactional
    public void editReportStatus(Integer reportNo, String status) {
        Report report = reportRepository.findWithReporterByReportNo(reportNo)
                .orElseThrow(() -> new RuntimeException("Report가 없습니다."));
        report.setStatus(status);
        reportRepository.save(report);
    }

    //유저 관리
    private ManagerMemberResponse toManagerMemberResponse(AllMemberListVO member) {
        return ManagerMemberResponse.builder()
                .memberNo(member.getMemberNo())
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .banDate(member.getBanDate())
                .imageUrl(member.getImageUrl())
                .accountStatus(member.getAccountStatus())
                .build();
    }

    public List<ManagerMemberResponse> getMemberList(String startDate, String endDate, String keyword) {
        return managerDAO.getAllMemberList(startDate, endDate, keyword)
                .stream()
                .map(this::toManagerMemberResponse)
                .toList();
    }

    @Transactional
    public void updateBanDate(Integer memberNo, Integer days) {
        Member member = memberRepository.findByMemberNo(memberNo);
        LocalDateTime banDate = LocalDateTime.now().plusDays(days);
        member.setBanDate(banDate);
    }

}
