package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByMemberId(String memberId);
    //service에서 passwordEncoder.matches(rawPassword, encodedPassword) 하기
    boolean existsByNickname(String nickname);
    Member findByMemberNo(Integer memberNo);
    List<Member> memberNo(Integer memberNo);
}