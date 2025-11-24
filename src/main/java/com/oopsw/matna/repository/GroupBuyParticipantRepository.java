package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.GroupBuyParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupBuyParticipantRepository extends JpaRepository<GroupBuyParticipant,Integer> {
    GroupBuyParticipant findByGroupBuy_GroupBuyNoAndParticipant_MemberNo(Integer groupBuyNo, Integer participantNo);
}
