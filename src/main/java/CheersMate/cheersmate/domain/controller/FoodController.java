package CheersMate.cheersmate.domain.controller;

import CheersMate.cheersmate.domain.dto.FoodDTO;
import CheersMate.cheersmate.domain.service.FoodService;
import CheersMate.cheersmate.jwt.JwtTokenUtil;
import CheersMate.cheersmate.users.dto.ApiResponse;
import CheersMate.cheersmate.users.entity.Role;
import CheersMate.cheersmate.users.entity.Users;
import CheersMate.cheersmate.users.service.UserService;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private final UserService userService;

    // 음식 조회
    @GetMapping
    public ResponseEntity<?> getAllFoods(@RequestHeader("Authorization") String token) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(0, 403));
        }

        List<FoodDTO> foods = foodService.getAllFoods();
        log.info("{\"result\": 1, \"resultCode\": 200, \"foods\": {}}", foods);
        return ResponseEntity.ok(new ApiResponse2(1, 200, foods));
    }

    // 음식 개별 조회
    @GetMapping("/{foodId}")
    public ResponseEntity<?> getFoods(@RequestHeader("Authorization") String token, @PathVariable("foodId") Long foodId) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(0, 403));
        }

        FoodDTO food = foodService.getFoodById(foodId);
        log.info("{\"result\": 1, \"resultCode\": 200, \"food\": {}}", food);
        return ResponseEntity.ok(new ApiResponse3(1, 200, food));
    }

    // 카테고리별 음식 조회
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<?> getFoodsByCategory(@RequestHeader("Authorization") String token, @PathVariable("categoryName") String categoryName) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(0, 403));
        }

        // `getFoodsByCategoryId` 대신 `getFoodsByCategory` 호출
        List<FoodDTO> foods = foodService.getFoodsByCategory(categoryName);
        log.info("{\"result\": 1, \"resultCode\": 200, \"foods\": {}}", foods);
        return ResponseEntity.ok(new ApiResponse2(1, 200, foods));
    }

    // 음식 추가
    @PostMapping
    public ResponseEntity<?> addFood(@RequestHeader("Authorization") String token, @RequestBody FoodDTO foodDTO) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(0, 403));
        }

        foodService.addFood(foodDTO);
        log.info("{\"result\": 1, \"resultCode\": 200}");
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(1, 200));
    }

    // 음식 수정
    @PutMapping("/{foodId}")
    public ResponseEntity<?> updateFood(@RequestHeader("Authorization") String token, @PathVariable("foodId") Long foodId, @RequestBody FoodDTO foodDTO) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(0, 403));
        }

        foodService.updateFood(foodId, foodDTO);
        log.info("{\"result\": 1, \"resultCode\": 200}");
        return ResponseEntity.ok(new ApiResponse(1, 200));
    }

    // 음식 삭제
    @DeleteMapping("/{foodId}")
    public ResponseEntity<?> deleteFood(@RequestHeader("Authorization") String token, @PathVariable("foodId") Long foodId) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(0, 403));
        }

        foodService.deleteFood(foodId);
        log.info("{\"result\": 1, \"resultCode\": 200}");
        return ResponseEntity.ok(new ApiResponse(1, 200));
    }

    // 관리자 인증 메서드
    private boolean isAdmin(String token) {
        token = token.substring(7);
        String userEmail = jwtTokenUtil.extractUsername(token);
        Users user = userService.findUserByEmail(userEmail);
        return user != null && user.getRole() == Role.ADMIN;
    }

    @Getter
    @NoArgsConstructor
    public static class ApiResponse2 {
        private int result;
        private int resultCode;
        private List<FoodDTO> foods;

        public ApiResponse2(int result, int resultCode, List<FoodDTO> foods) {
            this.result = result;
            this.resultCode = resultCode;
            this.foods = foods;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ApiResponse3 {
        private int result;
        private int resultCode;
        private FoodDTO food;

        public ApiResponse3(int result, int resultCode, FoodDTO food) {
            this.result = result;
            this.resultCode = resultCode;
            this.food = food;
        }
    }
}
