package com.oopsw.matna.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.oopsw.matna.auth.PrincipalDetails;
import com.oopsw.matna.repository.MemberRepository;
import com.oopsw.matna.repository.entity.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

public class JwtBasicAuthenticationFilter extends BasicAuthenticationFilter {
    private MemberRepository memberRepository;
    private final String jwtSecretKey;

    public JwtBasicAuthenticationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository, String jwtSecretKey) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.jwtSecretKey = jwtSecretKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwtToken = request.getHeader(JwtProperties.HEADER_STRING);

        if (jwtToken == null || !jwtToken.trim().startsWith(JwtProperties.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        jwtToken = jwtToken.replace(JwtProperties.TOKEN_PREFIX, "");

        var verifiedToken = JWT.require(Algorithm.HMAC512(jwtSecretKey))
                .build().verify(jwtToken);

        String memberId = verifiedToken.getClaim("memberId").asString();
        Date expiresAt = verifiedToken.getClaim("exp").asDate();

        if(memberId!=null){
            Member member = memberRepository.findByMemberId(memberId);
            PrincipalDetails principalDetails = new PrincipalDetails(member);
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            refreshToken(response, principalDetails, expiresAt);
        }
        chain.doFilter(request, response);
    }

    private void refreshToken(HttpServletResponse response, PrincipalDetails principalDetails, Date expiresAt){
        long remainTime = expiresAt.getTime() - System.currentTimeMillis();

        if (remainTime < JwtProperties.TOKEN_REFRESH_THRESHOLD) {
            String newJwt = JWT.create()
                    .withSubject(principalDetails.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRES_IN))
                    .withClaim("memberNo", principalDetails.getMemberNo())
                    .withClaim("memberId", principalDetails.getUsername())
                    .sign(Algorithm.HMAC512(jwtSecretKey));

            response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + newJwt);
        }
    }
}
