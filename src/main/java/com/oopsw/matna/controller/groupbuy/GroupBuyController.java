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
        return "groupBuyList";
    }

    @GetMapping("/periodGroupBuy/detail/{periodGroupBuyNo}")
    public String getPeriodDetail(@PathVariable Integer periodGroupBuyNo) {
        return "periodGroupBuyDetail";
    }

    @GetMapping("/quantityGroupBuy/detail/{quantityGroupBuyNo}")
    public String getQuantityDetail(@PathVariable Integer quantityGroupBuyNo) {
        return "quantityGroupBuyDetail";
    }

    @GetMapping("/periodGroupBuy/register")
    public String periodGroupBuyRegister() {
        return "addPeriodGroupBuy";
    }

    @GetMapping("/quantityGroupBuy/register")
    public String quantityGroupBuyRegister() {
        return "addQuantityGroupBuy";
    }

}
