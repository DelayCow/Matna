package com.oopsw.matna.controller.manager;

import com.oopsw.matna.auth.PrincipalDetails;
import com.oopsw.matna.dto.ManagerGroupBuyResponse;
import com.oopsw.matna.dto.ManagerIngredientResponse;
import com.oopsw.matna.dto.ManagerMemberResponse;
import com.oopsw.matna.dto.ManagerReportResponse;
import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.repository.entity.Report;
import com.oopsw.matna.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager")
public class ManagerRestController {
    private final ManagerService managerService;

    //사이드바
    @GetMapping("/sidebar")
    public String sidebar(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return principalDetails.getMemberNickname();
    }

    //재료 관리
    @GetMapping("/ingredientManagement")
    public List<ManagerIngredientResponse> getIngredient() {
        return managerService.getIngredients();
    }

    @GetMapping("/ingredientManagement/search")
    public List<ManagerIngredientResponse> getIngredientByKeyword(@RequestParam String keyword) {
        return managerService.getIngredientsByName(keyword);
    }

    @GetMapping("/ingredientManagement/notApproved")
    public List<ManagerIngredientResponse> getNotApprovedIngredients() {
        return managerService.getNotApprovedIngredients();
    }

    @PostMapping("/ingredientManagement")
    public ManagerIngredientResponse createIngredient(@RequestParam String ingredientName, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return managerService.addIngredient(principalDetails.getMemberNo(), ingredientName);
    }

//    @PostMapping("/ingredientManagement")
//    public ManagerIngredientResponse addIngredient(@RequestParam Integer creatorId, @RequestParam String ingredientName) {
//        return managerService.addIngredient(creatorId, ingredientName);
//    }

    @DeleteMapping("/ingredientManagement")
    public void deleteIngredient(@RequestParam Integer ingredientId) {
        managerService.deleteIngredient(ingredientId);
    }

    @PutMapping("/ingredientManagement")
    public void approveIngredient(@RequestParam Integer ingredientId) {
        managerService.approveIngredient(ingredientId);
    }

    @PutMapping("/ingredientManagement/change")
    public void changeApprovedIngredient(@RequestParam Integer ingredientNo ,@RequestParam Integer newIngredientNo) {
        managerService.changeIngredient(ingredientNo, newIngredientNo);
    }

    //공구 관리
    @GetMapping("/groupBuyManagement")
    public List<ManagerGroupBuyResponse> getGroupBuyList(String startDate, String endDate, String status, String title){
        return managerService.getGroupBuyList(startDate, endDate, status, title);
    }

    @PutMapping("/groupBuyManagement")
    public void cancelGroupBuy(@RequestParam Integer groupBuyNo) {
        managerService.cancelGroupBuy(groupBuyNo);
    }

    //신고 관리
    @GetMapping("/reportManagement")
    public List<ManagerReportResponse> getReportList(@RequestParam(required = false) LocalDate startDate,
                                                     @RequestParam(required = false) LocalDate endDate,
                                                     @RequestParam(required = false) String status,
                                                     @RequestParam(required = false, name = "reportCase") String reportCase,
                                                     @RequestParam(required = false) String keyword){
        return managerService.getReportList(startDate, endDate, status, reportCase, keyword);
    }

    @PutMapping("/reportManagement/complete")
    public void editReportComplete(@RequestParam Integer reportNo) {
        managerService.editReportStatus(reportNo, "complete");
    }

    @PutMapping("/reportManagement/rejection")
    public void editReportRejection(@RequestParam Integer reportNo) {
        managerService.editReportStatus(reportNo, "rejection");
    }

    //유저관리
    @GetMapping("/memberManagement")
    public List<ManagerMemberResponse> getMember(@RequestParam(required = false) String startDate,
                                                 @RequestParam(required = false) String endDate,
                                                 @RequestParam(required = false) String keyword) {
        return managerService.getMemberList(startDate, endDate, keyword);
    }

    @PutMapping("/memberManagement/ban")
    public void banMember(@RequestParam Integer memberNo, @RequestParam String days) {
        managerService.updateBanDate(memberNo, days);
    }
}
