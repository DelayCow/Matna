package com.oopsw.matna.controller.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagerController {

    @GetMapping("/ingredientManagement")
    public String ingredientManagement() {
        return "ingredientManagement";
    }

    @GetMapping("/groupBuyManagement")
    public String groupBuyManagement() {
        return "groupBuyManagement";
    }

    @GetMapping("/reportManagement")
    public String reportManagement() {
        return "reportManagement";
    }

    @GetMapping("memberManagement")
    public String memberManagement() {
        return "memberManagement";
    }
}
