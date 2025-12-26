package com.oopsw.matna.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oopsw.matna.auth.PrincipalDetails;
import com.oopsw.matna.vo.MemberVO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;
    private final String jwtSecretKey;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, String jwtSecretKey) {
        this.authenticationManager = authenticationManager;
        this.jwtSecretKey = jwtSecretKey;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            MemberVO inputData = mapper.readValue(request.getInputStream(), MemberVO.class);
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(inputData.getMemberId(), inputData.getPassword());
            Authentication auth = authenticationManager.authenticate(authRequest);
            return auth;
        } catch (IOException e) {
            throw new RuntimeException("로그인 요청 데이터 파싱 실패: " + e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        PrincipalDetails resultDetails = (PrincipalDetails) authResult.getPrincipal();

        String role = resultDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        String redirectUrl;

        if (role.equals("ROLE_ADMIN")) {
            redirectUrl = "/manager/ingredientManagement";
        } else {
            redirectUrl = "/";
        }

        String jwt = JWT.create().withSubject(resultDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRES_IN))
                .withClaim("memberNo", resultDetails.getMemberNo())
                .withClaim("memberId", resultDetails.getUsername())
                .sign(Algorithm.HMAC512(jwtSecretKey));

        Map<String, String> responseBody = Map.of(
                "message", "loginOk",
                "redirectUrl", redirectUrl
        );

        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwt);
        response.getWriter().println(new ObjectMapper().writeValueAsString(responseBody));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json;charset=UTF-8");

        String errorMessage = "아이디/비밀번호를 다시 입력해주세요.";

        if (failed instanceof InternalAuthenticationServiceException && failed.getCause() instanceof DisabledException) {
            errorMessage = failed.getCause().getMessage();
        } else if (failed instanceof DisabledException) {
            errorMessage = failed.getMessage();
        }

        Map<String, String> responseBody = Map.of(
                "message", "loginFail",
                "error", errorMessage
        );

        response.getWriter().println(new ObjectMapper().writeValueAsString(responseBody));
    }
}
