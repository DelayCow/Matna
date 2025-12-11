package com.oopsw.matna.controller.manager;

import com.oopsw.matna.dto.ManagerGroupBuyResponse;
import com.oopsw.matna.dto.ManagerIngredientResponse;
import com.oopsw.matna.dto.ManagerReportResponse;
import com.oopsw.matna.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager")
public class ManagerRestController {
    private final ManagerService managerService;

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
    public ManagerIngredientResponse addIngredient(@RequestParam Integer creatorId, @RequestParam String ingredientName) {
        return managerService.addIngredient(creatorId, ingredientName);
    }

    @DeleteMapping("/ingredientManagement")
    public void deleteIngredient(@RequestParam Integer ingredientId) {
        managerService.deleteIngredient(ingredientId);
    }

    @PutMapping("/ingredientManagement")
    public void approveIngredient(@RequestParam Integer ingredientId) {
        managerService.approveIngredient(ingredientId);
    }

    //공구 관리
    @GetMapping("/groupBuyManagement")
    public List<ManagerGroupBuyResponse> getGroupBuyList(String startDate, String endDate, String status, String title){
        return managerService.getGroupBuyList(startDate, endDate, status, title);
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

    @PutMapping("/reportManagement/complete?reportNo={reportNo}")
    public void editReportComplete(@PathVariable Integer reportNo) {
        managerService.editReportStatus(reportNo, "complete");
    }

    @PutMapping("/reportManagement/rejection?reportNo={reportNo}")
    public void editReportRejection(@PathVariable Integer reportNo) {
        managerService.editReportStatus(reportNo, "rejection");
    }
}
