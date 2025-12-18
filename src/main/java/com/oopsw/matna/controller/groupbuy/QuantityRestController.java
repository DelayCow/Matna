package com.oopsw.matna.controller.groupbuy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oopsw.matna.auth.PrincipalDetails;
import com.oopsw.matna.dto.QuantityDetailResponse;
import com.oopsw.matna.dto.QuantityListResponse;
import com.oopsw.matna.repository.entity.GroupBuyParticipant;
import com.oopsw.matna.repository.entity.QuantityGroupBuy;
import com.oopsw.matna.service.QuantityGroupBuyService;
import com.oopsw.matna.vo.QuantityGroupBuyDetailVO;
import com.oopsw.matna.vo.QuantityGroupBuyHomeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quantityGroupBuy")
public class QuantityRestController {
    private final QuantityGroupBuyService quantityGroupBuyService;
    private final ObjectMapper objectMapper;

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
    public ResponseEntity<QuantityDetailResponse> getQuantityGroupBuyDetail(
            @PathVariable Integer quantityGroupBuyNo,
            @AuthenticationPrincipal PrincipalDetails principalDetails) { // [1] 로그인 정보 받기


        Integer currentMemberNo = null;
        if (principalDetails != null) {
            currentMemberNo = principalDetails.getMemberNo();
        }


        Map<String, Object> serviceResultMap = quantityGroupBuyService.getQuantityGroupBuyDetail(quantityGroupBuyNo, currentMemberNo);


        QuantityGroupBuyDetailVO detailVO = (QuantityGroupBuyDetailVO) serviceResultMap.get("groupBuyDetail");

        List<Map<String, Object>> participantMapList = (List<Map<String, Object>>) serviceResultMap.get("participants");
        List<QuantityDetailResponse.ParticipantInfo> participantList = participantMapList.stream()
                .map(map -> QuantityDetailResponse.ParticipantInfo.builder()
                        .groupParticipantNo((Integer) map.get("groupParticipantNo"))
                        .memberNo((Integer) map.get("memberNo"))
                        .nickname((String) map.get("nickname"))
                        .profileUrl((String) map.get("profileUrl"))
                        .participatedDate((LocalDateTime) map.get("participatedDate"))
                        .myQuantity((Integer) map.get("myQuantity"))
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
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> addQuantityGroupBuy(
            @RequestPart("quantityRegisterRequest") String registerRequestJson,
            @RequestPart(value = "thumbnailFile") MultipartFile thumbnailFile) throws IOException {
        Map<String, Object> response = new HashMap<>();

            QuantityRegisterRequest request = objectMapper.readValue(
                    registerRequestJson,
                    QuantityRegisterRequest.class
            );

            QuantityGroupBuy quantityGroupBuy = quantityGroupBuyService.addQuantityGroupBuy(request, thumbnailFile);

            response.put("success", true);
            response.put("message", "공동구매가 성공적으로 등록되었습니다.");
            response.put("data", Map.of(
                    "periodGroupBuyNo", quantityGroupBuy.getQuantityGroupBuyNo(),
                    "groupBuyNo", quantityGroupBuy.getGroupBuy().getGroupBuyNo(),
                    "title", quantityGroupBuy.getGroupBuy().getTitle(),
                    "imageUrl", quantityGroupBuy.getGroupBuy().getImageUrl()
            ));

            return ResponseEntity.ok(response);
    }

    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> addParticipantToPeriodGroupBuy(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody GroupBuyParticipantRequest request){

        Integer currentMemberNo = principalDetails.getMemberNo();
        request.setParticipantNo(currentMemberNo);

        GroupBuyParticipant participant = quantityGroupBuyService.addParticipantToQuantityGroupBuy(request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "수량공구 참여가 완료되었습니다.");
        response.put("data", Map.of("groupParticipantNo", participant.getGroupParticipantNo(),
                "groupBuyNo", participant.getGroupBuy().getGroupBuyNo(),
                "participantNo", participant.getParticipant().getMemberNo(),
                "myQuantity", participant.getMyQuantity(),
                "initialPaymentPoint", participant.getInitialPaymentPoint(),
                "participatedDate", participant.getParticipatedDate()
        ));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/quantityModify/{groupBuyParticipantNo}")
    public ResponseEntity<Map<String, Object>> editModifyMyQuantity(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Integer groupBuyParticipantNo,
            @RequestBody Map<String, Integer> request) {

        Integer currentMemberNo = principalDetails.getMemberNo();
        Integer newQuantity = request.get("newQuantity");

        quantityGroupBuyService.editModifyMyQuantity(groupBuyParticipantNo, currentMemberNo, newQuantity);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "참여 수량이 성공적으로 수정되었습니다."
        ));
    }

    @PutMapping("/cancelParticipant/{groupBuyParticipantNo}")
    public ResponseEntity<Map<String, Object>> editCancelParticipantGroupBuy(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Integer groupBuyParticipantNo) {

        Integer currentMemberNo = principalDetails.getMemberNo();
        quantityGroupBuyService.editCancelParticipantGroupBuy(groupBuyParticipantNo, currentMemberNo);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "공동구매 참여가 성공적으로 취소되었습니다."
        ));
    }

    @PutMapping("/cancelCreator/{groupBuyNo}")
    public ResponseEntity<Map<String, Object>> editQuantityCreatorCancelAndRefund(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Integer groupBuyNo,
            @RequestBody Map<String, String> request) {

        Integer currentMemberNo = principalDetails.getMemberNo();
        String cancelReason = request.get("cancelReason");

        if (cancelReason == null || cancelReason.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "취소 사유를 입력해주세요."));
        }

        quantityGroupBuyService.editQuantityCreatorCancelAndRefund(groupBuyNo, currentMemberNo, cancelReason);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "공동구매가 취소되고 모든 참여자에게 환불이 완료되었습니다."
        ));
    }

    @PutMapping("/forcedCreator/{groupBuyNo}")
    public ResponseEntity<Map<String, Object>> editForcedCreatorAndStatusToClosed(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Integer groupBuyNo) {

        Integer currentMemberNo = principalDetails.getMemberNo();

        quantityGroupBuyService.editForcedCreatorAndStatusToClosed(groupBuyNo, currentMemberNo);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "개설자에 의해 공동구매가 진행되었습니다."
        ));
    }
}
