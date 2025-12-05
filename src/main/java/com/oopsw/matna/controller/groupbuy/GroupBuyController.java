package com.oopsw.matna.controller.groupbuy;

import com.oopsw.matna.auth.PrincipalDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public String getPeriodDetail(@PathVariable Integer periodGroupBuyNo, Model model) {
        model.addAttribute("periodGroupBuyNo", periodGroupBuyNo);
        return "/periodGroupBuyDetail";
    }

    @GetMapping("/quantityGroupBuy/detail/{quantityGroupBuyNo}")
    public String getQuantityDetail(@PathVariable Integer quantityGroupBuyNo, Model model) {
        model.addAttribute("quantityGroupBuyNo", quantityGroupBuyNo);
        return "/quantityGroupBuyDetail";
    }

    @GetMapping("/periodGroupBuy/register")
    public String periodGroupBuyRegister(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        model.addAttribute("creatorNo", principalDetails.getMemberNo());
        return "/addPeriodGroupBuy";
    }

}
