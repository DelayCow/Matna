package com.oopsw.matna.config;

import com.oopsw.matna.jwt.JwtAuthenticationFilter;
import com.oopsw.matna.jwt.JwtBasicAuthenticationFilter;
import com.oopsw.matna.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    @Autowired
    private CorsFilter corsFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration ac) throws Exception {//수동으로 작업해서 추가됨
        return ac.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/css/**",
                "/js/**",
                "/img/**"
        );
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager am,  MemberRepository memberRepository, @Value("${jwt.secret}") String jwtSecretKey) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(am, jwtSecretKey);
        jwtAuthenticationFilter.setFilterProcessesUrl("/api/auth/login");
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.formLogin(form -> form.disable());
        http.logout(logout -> logout.disable());
        http.httpBasic(httpBasic -> httpBasic.disable());
        http.addFilter(corsFilter);
        http.addFilter(jwtAuthenticationFilter);
        http.addFilter(new JwtBasicAuthenticationFilter(am, memberRepository, jwtSecretKey));
        http.authorizeHttpRequests(auth ->
                auth.requestMatchers("/login", "/register", "/member", "/api/auth/**").permitAll()
                    .requestMatchers("/manager/**").hasRole("ADMIN")
                    .anyRequest().authenticated());
        http.exceptionHandling(handling -> handling
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                })
        );
        return http.build();
    }
}
