package com.oopsw.matna.controller.ingredient;

import com.oopsw.matna.repository.entity.Ingredient;
import com.oopsw.matna.service.PeriodGroupBuyService;
import com.oopsw.matna.dto.IngredientResponse;
import com.oopsw.matna.repository.entity.Ingredient;
import com.oopsw.matna.service.IngredientService;
import com.oopsw.matna.service.PeriodGroupBuyService;
import com.oopsw.matna.vo.IngredientVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class IngredientRestController {
    private final IngredientService ingredientService;
    private final PeriodGroupBuyService periodGroupBuyService;

    @GetMapping("/ingredients")
    public List<IngredientResponse> findIngredientByKeyword(@RequestParam String keyword) {
        List<IngredientVO> ingredients = ingredientService.findIngredientByKeyword(keyword);
        return ingredients.stream().map(ingredient -> IngredientResponse.builder()
                .ingredientNo(ingredient.getIngredientNo())
                .ingredientName(ingredient.getIngredientName())
                .build()).collect(Collectors.toList());
    };

    @GetMapping("/ingredients/search")
    public ResponseEntity<?> getIngredients(@RequestParam String keyword) {
        try {
            List<Ingredient> ingredients = periodGroupBuyService.getIngredientKeyword(keyword);
            List<Map<String, Object>> result = ingredients.stream()
                    .map(ingredient -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("ingredientNo", ingredient.getIngredientNo());
                        map.put("ingredientName", ingredient.getIngredientName());
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("재료 검색 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/ingredients/add")
    public ResponseEntity<?> addIngredient(
            @RequestParam Integer creatorNo,
            @RequestParam String ingredientName) {
        try {
            Ingredient ingredient = periodGroupBuyService.addIngredient(
                    creatorNo,
                    ingredientName
            );
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "재료가 성공적으로 추가되었습니다.");
            response.put("data", Map.of(
                    "ingredientNo", ingredient.getIngredientNo(),
                    "ingredientName", ingredient.getIngredientName()
            ));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "이미 등록된 재료입니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
}
