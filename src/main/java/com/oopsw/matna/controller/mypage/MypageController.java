package com.oopsw.matna.controller.mypage;

import com.oopsw.matna.service.MypageService;
import com.oopsw.matna.vo.GroupBuyParticipantVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class MypageController {
    @GetMapping("/mypage")
    public String mypage() {
        return "mypage";
    }

}
