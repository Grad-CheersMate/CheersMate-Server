package CheersMate.cheersmate.domain.controller;


import CheersMate.cheersmate.domain.dto.LiquorDTO;
import CheersMate.cheersmate.domain.service.LiquorService;
import CheersMate.cheersmate.jwt.JwtTokenUtil;
import CheersMate.cheersmate.response.ApiResponse;
import CheersMate.cheersmate.response.ErrorResponse;
import CheersMate.cheersmate.response.LiquorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LiquorController {

    private final LiquorService liquorService;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping("api/liquors/category")
    public ResponseEntity<?> getLiquorsByCategoryPaged(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        Page<LiquorDTO> liquorsByCategoryPaged = liquorService.getLiquorsByCategoryPaged(category, page, size);

        if (liquorsByCategoryPaged.isEmpty()) {
            log.info("{\"result\": 0, \"httpCode\": 404, \"message\": \"Category not found or no liquors in this category\"}");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(false, 404, "Category not found or no liquors in this category"));
        }

        log.info("{\"result\": 1, \"httpCode\": 200, \"liquors\": {}}", liquorsByCategoryPaged);
        return ResponseEntity.ok(new LiquorResponse(true, 200, liquorsByCategoryPaged));
    }

    @GetMapping("api/liquors/{id}")
    public ResponseEntity<?> getLiquorById(@PathVariable("id") Long id) {
        Optional<LiquorDTO> liquor = liquorService.getLiquorById(id);
        if (liquor.isPresent()) {
            log.info("{\"result\": 1, \"httpCode\": 200, \"liquor\": {}}", liquor);
            return ResponseEntity.ok(new LiquorResponse(true, 200, liquor.get()));
        } else {
            log.info("{\"result\": 0, \"httpCode\": 600}");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, 600));
        }
    }

    @PostMapping("api/admin/liquors")
    public ResponseEntity<?> createLiquor(@RequestHeader("Authorization") String token, @RequestBody LiquorDTO liquorDTO) {
        if (!isAdmin(token)) {
            String message = "Access denied: User does not have admin privileges.";
            log.info("{\"result\": 0, \"httpCode\": 403, \"message\": \"{}\"}", message);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(false, 403, message));
        }

        liquorService.createLiquor(liquorDTO);
        log.info("{\"result\": 1, \"httpCode\": 200}");
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, 200));
    }

    @PutMapping("api/admin/liquors/{id}")
    public ResponseEntity<?> updateLiquor(@RequestHeader("Authorization") String token, @PathVariable("id") Long id, @RequestBody LiquorDTO liquorDTO) {
        if (!isAdmin(token)) {
            String message = "Access denied: User does not have admin privileges.";
            log.info("{\"result\": 0, \"httpCode\": 403, \"message\": \"{}\"}", message);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(false, 403, message));
        }

        liquorService.updateLiquor(id, liquorDTO);
        log.info("{\"result\": 1, \"httpCode\": 200}");
        return ResponseEntity.ok(new ApiResponse(true, 200));
    }

    @DeleteMapping("api/admin/liquors/{id}")
    public ResponseEntity<?> deleteLiquor(@RequestHeader("Authorization") String token, @PathVariable("id") Long id) {
        if (!isAdmin(token)) {
            String message = "Access denied: User does not have admin privileges.";
            log.info("{\"result\": 0, \"httpCode\": 403, \"message\": \"{}\"}", message);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(false, 403, message));
        }

        liquorService.deleteLiquor(id);
        log.info("{\"result\": 1, \"httpCode\": 200}");
        return ResponseEntity.ok(new ApiResponse(true, 200));
    }

    // 관리자 인증 메서드
    private boolean isAdmin(String token) {
        String role = jwtTokenUtil.extractRole(token.substring(7));
        return "ADMIN".equals(role);
    }
}