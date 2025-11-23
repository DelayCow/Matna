package com.oopsw.matna.dao;

import com.oopsw.matna.vo.GroupBuyHomeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GroupBuyHomeDAO {
    List<GroupBuyHomeVO> selectGroupBuyListForHome(Map<String, Object> params);
}
