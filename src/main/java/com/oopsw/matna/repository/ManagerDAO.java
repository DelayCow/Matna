package com.oopsw.matna.repository;

import com.oopsw.matna.vo.AllGroupBuyListVO;
import com.oopsw.matna.vo.AllMemberListVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface ManagerDAO {
    List<AllGroupBuyListVO> getAllGroupBuyList(
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("title") String title,
        @Param("status") String status
    );

    List<AllMemberListVO> getAllMemberList(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("keyword") String keyword
    );
}
