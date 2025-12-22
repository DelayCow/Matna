package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    boolean existsByNickname(String nickname);
    Member findByMemberId(String memberId);
    Member findByMemberNo(Integer memberNo);
    Member findByMemberIdAndDelDateIsNullAndBanDateIsNull(String memberId);

}
