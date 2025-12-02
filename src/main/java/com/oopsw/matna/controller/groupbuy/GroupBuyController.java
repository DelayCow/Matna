package com.oopsw.matna.controller.groupbuy;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GroupBuyController {
    @GetMapping("/groupBuy")
    public String groupbuy() {
        return "/groupBuyList";
    }

    @GetMapping("/periodGroupBuy/detail/{periodGroupBuyNo}")
    public String getDetail(@PathVariable Integer periodGroupBuyNo, Model model) {
        model.addAttribute("periodGroupBuyNo", periodGroupBuyNo);
        return "periodGroupBuyDetail";
    }
}
