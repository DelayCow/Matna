package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.GroupBuy;
import com.oopsw.matna.repository.entity.GroupBuyParticipant;
import com.oopsw.matna.repository.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupBuyParticipantRepository extends JpaRepository<GroupBuyParticipant,Integer> {
    GroupBuyParticipant findByGroupBuy_GroupBuyNoAndParticipant_MemberNo(Integer groupBuyNo, Integer participantNo);

    List<GroupBuyParticipant> findByGroupParticipantNoOrderByParticipatedDateAsc(Integer groupParticipantNo);

    List<GroupBuyParticipant> findByGroupBuy(GroupBuy groupBuy);
    List<GroupBuyParticipant> findByGroupBuyAndCancelDateIsNullOrderByParticipatedDateAsc(GroupBuy groupBuy);

    Optional<GroupBuyParticipant> findByGroupBuy_GroupBuyNoAndParticipant_MemberNoAndCancelDateIsNull(Integer groupBuyNo, Integer participantNo);

    boolean existsByGroupBuyAndParticipantAndCancelDateIsNull(GroupBuy groupBuy, Member participant);

    List<GroupBuyParticipant> findByGroupBuyAndCancelDateIsNull(GroupBuy groupBuy);
}