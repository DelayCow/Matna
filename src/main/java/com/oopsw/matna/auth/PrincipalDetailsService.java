package com.oopsw.matna.auth;

import com.oopsw.matna.repository.MemberRepository;
import com.oopsw.matna.repository.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByMemberIdAndDelDateIsNull(username);

        if (member == null) {
            throw new UsernameNotFoundException("존재하지 않는 회원입니다 : " + username);
        }

        if (member.getBanDate() != null && member.getBanDate().isAfter(LocalDateTime.now())) {
            String formattedDate = member.getBanDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            throw new DisabledException("정지된 계정입니다. 정지 기한: " + formattedDate);
        }

        return new PrincipalDetails(member);
    }

}
