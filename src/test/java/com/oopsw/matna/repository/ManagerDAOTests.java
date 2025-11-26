package com.oopsw.matna.repository;

import com.oopsw.matna.vo.AllGroupBuyListVO;
import com.oopsw.matna.vo.AllMemberListVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ManagerDAOTests {

    @Autowired
    ManagerDAO managerDAO;

    @Test
    public void getAllGroupBuyListTest() {
        //검색 안넘어오면 null로 넣기
        String startDate = "2024-11-01";
        String endDate = "2025-11-30";
        String status = null;
        String title = "아";
        List<AllGroupBuyListVO> list = managerDAO.getAllGroupBuyList(startDate, endDate, title, status);
        System.out.println(list.size());//4개
    }

    @Test
    public void getAllMemberListTest() {
        String startDate = "2023-11-01";
        String endDate = "2023-11-30";
        String keyword = "관리자";
        List<AllMemberListVO> list = managerDAO.getAllMemberList(startDate, endDate, keyword);
        System.out.println(list.size());//4개
    }
}
