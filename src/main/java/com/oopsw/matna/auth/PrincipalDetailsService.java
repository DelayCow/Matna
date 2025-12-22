package com.oopsw.matna.auth;

import com.oopsw.matna.repository.MemberRepository;
import com.oopsw.matna.repository.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByMemberIdAndDelDateIsNullAndBanDateIsNull(username);

        if (member == null) {
            throw new UsernameNotFoundException("존재하지 않는 회원이거나 이용이 제한된 계정입니다 : " + username);
        }

        return new PrincipalDetails(member);
    }

}
