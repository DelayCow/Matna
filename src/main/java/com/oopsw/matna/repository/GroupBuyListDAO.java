package com.oopsw.matna.repository;

import com.oopsw.matna.vo.GroupBuyListVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupBuyListDAO {
    List<GroupBuyListVO> getParticipantList(Integer participantNo);
    List<GroupBuyListVO> getCreateGroupBuyList(Integer creatorNo);
}
