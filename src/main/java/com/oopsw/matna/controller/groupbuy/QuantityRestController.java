package com.oopsw.matna.controller.groupbuy;

import com.oopsw.matna.dto.QuantityListResponse;
import com.oopsw.matna.service.QuantityGroupBuyService;
import com.oopsw.matna.vo.QuantityGroupBuyHomeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
