package com.oopsw.matna.service;

import com.oopsw.matna.repository.*;
import com.oopsw.matna.repository.entity.*;
import com.oopsw.matna.vo.GroupBuyParticipantVO;
import com.oopsw.matna.vo.PeroidGroupBuyCreateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PeriodGroupBuyService {
    private final IngredientRepository ingredientRepository;
    private final MemberRepository memberRepository;
    private final GroupBuyRepository groupBuyRepository;
    private final PeriodGroupBuyRepository periodGroupBuyRepository;
    private final GroupBuyParticipantRepository groupBuyParticipantRepository;


    public List<Ingredient> getIngredientKeyword(String keyword){
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("검색 키워드는 비어있을 수 없습니다.");
        }
        return ingredientRepository.findByIngredientNameContaining(keyword);
    }

    public Ingredient addIngredient(Integer creatorNo, String ingredientName) {
        // 생성자 존재 여부 확인
        Member creatorMember = memberRepository.findById(creatorNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. 회원번호: " + creatorNo));
        // 중복 재료명 검증
        if (ingredientRepository.existsByIngredientName(ingredientName.trim())) {
            throw new IllegalStateException("이미 존재하는 재료명입니다: " + ingredientName);
        }
        Ingredient ingredient = Ingredient.builder()
                .ingredientName(ingredientName.trim()) //공백제거
                .creator(creatorMember)
                .inDate(LocalDateTime.now())
                .build();

        return ingredientRepository.save(ingredient);
    }

    @Transactional
    public PeriodGroupBuy addPeriodGroupBuy(PeroidGroupBuyCreateVO vo) {
        // 재료 존재 여부 확인
        Ingredient ingredient = ingredientRepository.findById(vo.getIngredientNo())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 재료입니다. 재료번호: " + vo.getIngredientNo()));
        // 생성자 존재 여부 확인
        Member creator = memberRepository.findById(vo.getCreatorNo())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. 회원번호: " + vo.getCreatorNo()));
        // GroupBuy 생성 및 저장
        GroupBuy groupBuy = groupBuyRepository.save(
                GroupBuy.builder()
                        .ingredient(ingredient)
                        .creator(creator)
                        .title(vo.getTitle().trim())
                        .buyEndDate(vo.getBuyEndDate())
                        .shareEndDate(vo.getShareEndDate())
                        .shareTime(vo.getShareTime())
                        .shareLocation(vo.getShareLocation())
                        .shareDetailAddress(vo.getShareDetailAddress())
                        .price(vo.getPrice())
                        .quantity(vo.getQuantity())
                        .unit(vo.getUnit())
                        .feeRate(vo.getFeeRate())
                        .imageUrl(vo.getImageUrl())
                        .content(vo.getContent())
                        .inDate(LocalDateTime.now())
                        .itemSaleUrl(vo.getItemSaleUrl())
                        .scrapCount(0)
                        .status("open")
                        .build()
        );
        PeriodGroupBuy periodGroupBuy = periodGroupBuyRepository.save(
                PeriodGroupBuy.builder()
                        .groupBuy(groupBuy)
                        .dueDate(vo.getDueDate())
                        .maxParticipants(vo.getMaxParticipants())
                        .build()
        );
        return periodGroupBuy;
    }

    public GroupBuyParticipant addParticipantToPeriodGroupBuy(GroupBuyParticipantVO vo) {
        // 참여자 존재 여부 확인
        Member participantMember = memberRepository.findById(vo.getParticipantNo())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. 회원번호: " + vo.getParticipantNo()));
        // 공동구매 존재 여부 확인
        GroupBuy groupBuyNo = groupBuyRepository.findById(vo.getGroupBuyNo())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공동구매입니다. 공구번호: " + vo.getGroupBuyNo()));

        Integer price = groupBuyNo.getPrice();
        Integer feeRate = groupBuyNo.getFeeRate();
        int initialPaymentPoint = (int) Math.round((price * (1.0 + (feeRate / 100.0))) / 2.0);

        GroupBuyParticipant joinPeriodGroupBuy = groupBuyParticipantRepository.save(
                GroupBuyParticipant.builder()
                        .participant(participantMember)
                        .groupBuy(groupBuyNo)
                        .participatedDate(LocalDateTime.now())
                        .initialPaymentPoint(initialPaymentPoint)
                        .build()
        );
        return joinPeriodGroupBuy;
    }


}
