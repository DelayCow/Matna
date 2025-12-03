package com.oopsw.matna.dao;

import com.oopsw.matna.vo.GroupBuyListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupBuyListDAO {
    List<GroupBuyListVO> getParticipantList(
            @Param("participantNo") Integer participantNo,
            @Param("filter") String filter
    );

    List<GroupBuyListVO> getCreateGroupBuyList(Integer creatorNo);
}
