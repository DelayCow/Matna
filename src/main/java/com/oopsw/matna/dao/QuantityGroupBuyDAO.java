package com.oopsw.matna.dao;


import com.oopsw.matna.vo.QuantityGroupBuyHomeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface QuantityGroupBuyDAO {
    List<QuantityGroupBuyHomeVO> selectQuantityGroupBuyHomeList(Map<String, Object> params);
}
