package com.oopsw.matna.controller.groupbuy;

import com.oopsw.matna.dto.PeriodDetailResponse;
import com.oopsw.matna.dto.QuantityDetailInfo;
import com.oopsw.matna.dto.QuantityDetailResponse;
import com.oopsw.matna.dto.QuantityListResponse;
import com.oopsw.matna.service.QuantityGroupBuyService;
import com.oopsw.matna.vo.QuantityGroupBuyDetailVO;
import com.oopsw.matna.vo.QuantityGroupBuyHomeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quantityGroupBuy")
public class QuantityRestController {
    private final QuantityGroupBuyService quantityGroupBuyService;

    @GetMapping("/home")
    public ResponseEntity<List<QuantityListResponse>> getQuantityGroupBuyHome(
            @RequestParam(required = false) String keyword, @RequestParam(required = false) String orderBy) {
        Map<String, Object> params = new HashMap<>();
        if (keyword != null && !keyword.isEmpty()) {
            params.put("keyword", keyword);
        }
        if (orderBy != null && !orderBy.isEmpty()) {
            params.put("orderBy", orderBy);
        }
        
        List<QuantityGroupBuyHomeVO> voList = quantityGroupBuyService.getQuantityGroupBuyHome(params);
        List<QuantityListResponse> responseList = voList.stream()
                .map(vo -> QuantityListResponse.builder()
                        .title(vo.getTitle())
                        .nickname(vo.getNickname())
                        .creatorImageUrl(vo.getCreatorImageUrl())
                        .groupBuyImageUrl(vo.getGroupBuyImageUrl())
                        .inDate(vo.getInDate())
                        .quantity(vo.getQuantity())
                        .remainingQty(vo.getRemainingQty())
                        .unit(vo.getUnit())
                        .pricePerUnit(vo.getPricePerUnit())
                        .myQuantity(vo.getMyQuantity())
                        .shareAmount(vo.getShareAmount())
                        .shareLocation(vo.getShareLocation())
                        .quantityGroupBuyNo(vo.getQuantityGroupBuyNo())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/detail/{quantityGroupBuyNo}")
    public ResponseEntity<QuantityDetailResponse> getQuantityGroupBuyDetail(@PathVariable Integer quantityGroupBuyNo) {
        try {
            Map<String, Object> serviceResultMap = quantityGroupBuyService.getQuantityGroupBuyDetail(quantityGroupBuyNo);
            QuantityGroupBuyDetailVO detailVO = (QuantityGroupBuyDetailVO) serviceResultMap.get("groupBuyDetail");

            List<Map<String, Object>> participantMapList = (List<Map<String, Object>>) serviceResultMap.get("participants");
            List<QuantityDetailResponse.ParticipantInfo> participantList = participantMapList.stream()
                    .map(map -> QuantityDetailResponse.ParticipantInfo.builder()
                            .nickname((String) map.get("nickname"))
                            .profileUrl((String) map.get("profileUrl"))
                            .participatedDate((LocalDateTime) map.get("participatedDate"))
                            .build())
                    .collect(Collectors.toList());

            List<Map<String, Object>> recipeMapList = (List<Map<String, Object>>) serviceResultMap.get("recipes");
            List<QuantityDetailResponse.RecipeInfo> recipeList = recipeMapList.stream()
                    .map(map -> QuantityDetailResponse.RecipeInfo.builder()
                            .recipeNo((Integer) map.get("recipeNo"))
                            .title((String) map.get("title"))
                            .imageUrl((String) map.get("imageUrl"))
                            .authorNickname((String) map.get("authorNickname"))
                            .inDate((LocalDateTime) map.get("inDate"))
                            .build())
                    .collect(Collectors.toList());

            QuantityDetailResponse response = QuantityDetailResponse.builder()
                    .groupBuyDetail(detailVO)
                    .participant(participantList)
                    .recipes(recipeList)
                    .build();

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            System.err.println("Error fetching quantity group buy detail: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
