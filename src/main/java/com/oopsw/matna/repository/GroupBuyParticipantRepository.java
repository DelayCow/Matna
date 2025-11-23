package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.GroupBuy;
import com.oopsw.matna.repository.entity.GroupBuyParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupBuyParticipantRepository extends JpaRepository<GroupBuyParticipant, Integer> {
    List<GroupBuyParticipant> findByGroupBuyAndCancelDateIsNull(GroupBuy groupBuy);
//    GroupBuyParticipant findByGroupParticipantNo(Integer groupParticipantNo);
//    List<GroupBuyParticipant> findByGroupBuyAndCancelDateIsNull(GroupBuy groupBuy);
}
