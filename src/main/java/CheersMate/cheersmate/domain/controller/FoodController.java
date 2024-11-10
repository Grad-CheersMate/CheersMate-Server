package CheersMate.cheersmate.domain.controller;

import CheersMate.cheersmate.domain.dto.FoodDTO;
import CheersMate.cheersmate.domain.service.FoodService;
import CheersMate.cheersmate.jwt.JwtTokenUtil;
import CheersMate.cheersmate.response.ApiResponse;
import CheersMate.cheersmate.response.ErrorResponse;
import CheersMate.cheersmate.response.FoodResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/food")
public class FoodController {
    private final FoodService foodService;
    private final JwtTokenUtil jwtTokenUtil;

    // 음식 조회
    @GetMapping
    public ResponseEntity<?> getAllFoods(@RequestHeader("Authorization") String token) {
        if (!isAdmin(token)) {
            String message = "Access denied: User does not have admin privileges.";
            log.info("{\"result\": 0, \"httpCode\": 403, \"message\": \"{}\"}", message);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(false, 403, message));
        }

        List<FoodDTO> foods = foodService.getAllFoods();
        log.info("{\"result\": 1, \"httpCode\": 200, \"foods\": {}}", foods);
        return ResponseEntity.ok(new FoodResponse(true, 200, foods));
    }

    // 음식 개별 조회
    @GetMapping("/{foodId}")
    public ResponseEntity<?> getFoods(@RequestHeader("Authorization") String token, @PathVariable("foodId") Long foodId) {
        if (!isAdmin(token)) {
            String message = "Access denied: User does not have admin privileges.";
            log.info("{\"result\": 0, \"httpCode\": 403, \"message\": \"{}\"}", message);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(false, 403, message));
        }

        FoodDTO food = foodService.getFoodById(foodId);
        log.info("{\"result\": 1, \"httpCode\": 200, \"food\": {}}", food);
        return ResponseEntity.ok(new FoodResponse(true, 200, food));
    }

    // 카테고리별 음식 조회
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<?> getFoodsByCategory(@RequestHeader("Authorization") String token, @PathVariable("categoryName") String categoryName) {
        if (!isAdmin(token)) {
            String message = "Access denied: User does not have admin privileges.";
            log.info("{\"result\": 0, \"httpCode\": 403, \"message\": \"{}\"}", message);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(false, 403, message));
        }

        List<FoodDTO> foods = foodService.getFoodsByCategory(categoryName);

        // 음식 목록이 비어 있는 경우 404 에러 응답
        if (foods.isEmpty()) {
            log.info("{\"result\": 0, \"httpCode\": 404, \"message\": \"Category not found or no foods in this category\"}");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(false, 404, "Category not found or no foods in this category"));
        }

        log.info("{\"result\": 1, \"httpCode\": 200, \"foods\": {}}", foods);
        return ResponseEntity.ok(new FoodResponse(true, 200, foods));
    }

    // 음식 추가
    @PostMapping
    public ResponseEntity<?> addFood(@RequestHeader("Authorization") String token, @RequestBody FoodDTO foodDTO) {
        if (!isAdmin(token)) {
            String message = "Access denied: User does not have admin privileges.";
            log.info("{\"result\": 0, \"httpCode\": 403, \"message\": \"{}\"}", message);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(false, 403, message));
        }

        foodService.addFood(foodDTO);
        log.info("{\"result\": 1, \"httpCode\": 200}");
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, 200));
    }

    // 음식 수정
    @PutMapping("/{foodId}")
    public ResponseEntity<?> updateFood(@RequestHeader("Authorization") String token, @PathVariable("foodId") Long foodId, @RequestBody FoodDTO foodDTO) {
        if (!isAdmin(token)) {
            String message = "Access denied: User does not have admin privileges.";
            log.info("{\"result\": 0, \"httpCode\": 403, \"message\": \"{}\"}", message);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(false, 403, message));
        }

        foodService.updateFood(foodId, foodDTO);
        log.info("{\"result\": 1, \"httpCode\": 200}");
        return ResponseEntity.ok(new ApiResponse(true, 200));
    }

    // 음식 삭제
    @DeleteMapping("/{foodId}")
    public ResponseEntity<?> deleteFood(@RequestHeader("Authorization") String token, @PathVariable("foodId") Long foodId) {
        if (!isAdmin(token)) {
            String message = "Access denied: User does not have admin privileges.";
            log.info("{\"result\": 0, \"httpCode\": 403, \"message\": \"{}\"}", message);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(false, 403, message));
        }

        foodService.deleteFood(foodId);
        log.info("{\"result\": 1, \"httpCode\": 200}");
        return ResponseEntity.ok(new ApiResponse(true, 200));
    }

    // 관리자 인증 메서드
    private boolean isAdmin(String token) {
        String role = jwtTokenUtil.extractRole(token.substring(7));
        return "ADMIN".equals(role);
    }
}