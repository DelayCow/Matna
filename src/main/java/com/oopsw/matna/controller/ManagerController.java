package com.oopsw.matna.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ManagerController {

    @GetMapping("/manager/ingredientManagement")
    public String manager() {
        return "/ingredientManagement";
    }
}
