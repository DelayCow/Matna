package com.oopsw.matna.controller.groupbuy;

import com.oopsw.matna.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class GroupBuyAuthRestController {

    @GetMapping("/currentUser")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Map<String, Object> response = new HashMap<>();

        if (principalDetails != null) {
            response.put("isAuthenticated", true);
            response.put("memberNo", principalDetails.getMemberNo());
            response.put("username", principalDetails.getUsername());
            // 필요한 추가 정보
            response.put("nickname", principalDetails.getMember().getNickname());
            response.put("profileImageUrl", principalDetails.getMember().getImageUrl());
        } else {
            response.put("isAuthenticated", false);
            response.put("memberNo", null);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAuth(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Map<String, Object> response = new HashMap<>();
        response.put("isAuthenticated", principalDetails != null);
        response.put("memberNo", principalDetails != null ? principalDetails.getMemberNo() : null);

        return ResponseEntity.ok(response);
    }
}

