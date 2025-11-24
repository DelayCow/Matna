package com.oopsw.matna.dao;

import com.oopsw.matna.vo.PeriodGroupBuyDetailVO;
import com.oopsw.matna.vo.PeriodGroupBuyHomeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PeriodGroupBuyDAO {
    List<PeriodGroupBuyHomeVO> selectGroupBuyListForHome(Map<String, Object> params);
    PeriodGroupBuyDetailVO selectPeriodGroupBuyDetail(Integer periodGroupBuyNo);
}
