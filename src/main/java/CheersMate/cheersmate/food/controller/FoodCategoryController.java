package CheersMate.cheersmate.food.controller;

import CheersMate.cheersmate.food.dto.FoodCategoryDTO;
import CheersMate.cheersmate.food.service.FoodCategoryService;
import CheersMate.cheersmate.jwt.JwtTokenUtil;
import CheersMate.cheersmate.users.dto.ApiResponse;
import CheersMate.cheersmate.users.entity.Role;
import CheersMate.cheersmate.users.entity.Users;
import CheersMate.cheersmate.users.service.UserService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/foodcate")
public class FoodCategoryController {
    private final FoodCategoryService foodCategoryService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    // 전체 카테고리 조회
    @GetMapping
    public ResponseEntity<?> getAllCategories(@RequestHeader("Authorization") String token) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(0, 403));
        }

        List<FoodCategoryDTO> categories = foodCategoryService.getAllCategories();
        return ResponseEntity.ok(new ApiResponse2(1, 200, categories));
    }

    // 개별 카테고리 조회
    @GetMapping("/{foodcateId}")
    public ResponseEntity<?> getCategoryById(@RequestHeader("Authorization") String token, @PathVariable("foodcateId") Long foodcateId) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(0, 403));
        }

        FoodCategoryDTO category = foodCategoryService.getCategoryById(foodcateId);
        return ResponseEntity.ok(new ApiResponse3(1, 200, category));
    }

    // 카테고리 추가
    @PostMapping
    public ResponseEntity<?> addCategory(@RequestHeader("Authorization") String token, @RequestBody FoodCategoryDTO categoryDTO) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(0, 403));
        }

        foodCategoryService.addCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(1, 200));
    }

    // 카테고리 수정
    @PutMapping("/{foodcateId}")
    public ResponseEntity<?> updateCategory(@RequestHeader("Authorization") String token, @PathVariable("foodcateId") Long foodcateId, @RequestBody FoodCategoryDTO categoryDTO) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(0, 403));
        }

        foodCategoryService.updateCategory(foodcateId, categoryDTO);
        return ResponseEntity.ok(new ApiResponse(1, 200));
    }

    // 카테고리 삭제
    @DeleteMapping("/{foodcateId}")
    public ResponseEntity<?> deleteCategory(@RequestHeader("Authorization") String token, @PathVariable("foodcateId") Long foodcateId) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(0, 403));
        }

        foodCategoryService.deleteCategory(foodcateId);
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
        private List<FoodCategoryDTO> categories;

        public ApiResponse2(int result, int resultCode, List<FoodCategoryDTO> categories) {
            this.result = result;
            this.resultCode = resultCode;
            this.categories = categories;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ApiResponse3 {
        private int result;
        private int resultCode;
        private FoodCategoryDTO category;

        public ApiResponse3(int result, int resultCode, FoodCategoryDTO category) {
            this.result = result;
            this.resultCode = resultCode;
            this.category = category;
        }
    }
}
