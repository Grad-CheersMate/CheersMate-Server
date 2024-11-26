package CheersMate.cheersmate.users.controller;

import CheersMate.cheersmate.exception.CustomValidationException;
import CheersMate.cheersmate.jwt.JwtTokenUtil;
import CheersMate.cheersmate.response.ApiResponse;
import CheersMate.cheersmate.response.ErrorResponse;
import CheersMate.cheersmate.response.UserResponse;
import CheersMate.cheersmate.users.dto.ChangePasswordDTO;
import CheersMate.cheersmate.users.dto.LoginDTO;
import CheersMate.cheersmate.users.dto.UserDTO;
import CheersMate.cheersmate.users.entity.Role;
import CheersMate.cheersmate.users.entity.Users;
import CheersMate.cheersmate.users.service.TokenService;
import CheersMate.cheersmate.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserController {
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    // 사용자 데이터 페이징
    @GetMapping("/api/admin/users/page")
    public ResponseEntity<?> getUsers(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(403).body("Access Denied");
        }

        Page<UserDTO> users = userService.getUsersWithPaging(page, size);
        return ResponseEntity.ok(new UserResponse(true, 200, users));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Missing or invalid refresh token"));
        }

        String token = refreshToken.substring(7);
        String email = jwtTokenUtil.getEmail(token);

        log.info("Received refresh token for email: {}", email);

        // Refresh Token 검증
        if (!tokenService.validateRefreshToken(email, token)) {
            log.info("Refresh Token validation failed for email: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Invalid or expired refresh token"));
        }

        Users user = userService.findUserByEmail(email);
        if (user == null) {
            log.info("User not found for email: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "User not found"));
        }

        String newAccessToken = jwtTokenUtil.generateAccessToken(user);
        log.info("New Access Token issued for email: {}", email);

        log.info("{\"result\": 1, \"httpCode\": 200, \"newAccessToken\": \"{}\"}", newAccessToken);
        return ResponseEntity.ok(new ApiResponse(true, 200, newAccessToken));
    }

    @PostMapping("/users/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO request) {
        Users user = userService.login(request.getEmail(), request.getPassword());

        if (user != null) {
            String accessToken = jwtTokenUtil.generateAccessToken(user);
            String refreshToken = jwtTokenUtil.generateRefreshToken(user);

            // Refresh Token 저장
            LocalDateTime expirationTime = LocalDateTime.now()
                    .plus(refreshExpiration, ChronoUnit.MILLIS); // 7일
            log.info("Refresh Token expiration set to: {}", expirationTime);

            tokenService.saveRefreshToken(user.getEmail(), refreshToken, expirationTime);
            log.info("Refresh Token saved for email {} with expiration time: {}", user.getEmail(), expirationTime);

            log.info("{\"result\": 1, \"httpCode\": 200, \"accessToken\": \"{}\", \"refreshToken\": \"{}\"}", accessToken, refreshToken);
            return ResponseEntity.ok(new ApiResponse(true, 200, accessToken, refreshToken));
        } else {
            log.info("{\"result\": 0, \"httpCode\": 600}");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,600));
        }
    }

    @PostMapping("/users/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String token) {
        try {
            // Bearer 토큰에서 실제 토큰 값 추출
            String email = jwtTokenUtil.extractUsername(token.substring(7));
            log.info("Extracted email: {}", email);

            // Refresh Token 삭제
            if (tokenService.validateRefreshToken(email, token.substring(7))) {
                tokenService.deleteRefreshToken(email);
                log.info("Successfully deleted refresh token for email: {}", email);
            } else {
                log.error("Invalid refresh token for email: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Invalid token"));
            }

            log.info("{\"result\": 1, \"httpCode\": 200}");
            return ResponseEntity.ok(new ApiResponse(true, 200));
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage(), e);
            return ResponseEntity.ok(new ApiResponse(false, 600));
        }
    }

    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserDTO request) {
        try {
            String encodedPassword = passwordEncoder.encode(request.getPassword());

            Users newUser = new Users();
            newUser.setNickname(request.getNickname());
            newUser.setPassword(encodedPassword);
            newUser.setEmail(request.getEmail());
            newUser.setTell(request.getTell());
            newUser.setRole(Role.USER);

            userService.saveUser(newUser);

            log.info("{\"result\": 1, \"httpCode\": 200}");
            return ResponseEntity.ok(new ApiResponse(true,200));
        } catch (CustomValidationException e) {
            log.error("Validation error during registration", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(false, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false,600));
        }
    }

    @PostMapping("/users/emailFind")
    public ResponseEntity<?> emailFind(@RequestBody UserDTO request) {
        try {
            Users user = userService.findEmail(request.getTell(), request.getNickname());

            if (user == null) {
                log.info("{\"result\": 0, \"httpCode\": 404}");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false,404));
            }

            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(user.getEmail());

            log.info("{\"result\": 1, \"httpCode\": 200, \"user\": \"{}\"}", userDTO);
            return ResponseEntity.ok(new UserResponse(true,200,userDTO));
        } catch (Exception e) {
            log.error("Error during email find", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false,600));
        }
    }

    @PostMapping("/users/passFind")
    public ResponseEntity<?> passFind(@RequestBody UserDTO request) {
        try {
            Users user = userService.findPass(request.getEmail(), request.getTell());

            if (user == null) {
                log.info("{\"result\": 0, \"httpCode\": 404}");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false,404));
            }

            String tempPassword = userService.generateTempPassword();
            userService.changePassword(user.getEmail(), tempPassword);

            UserDTO userDTO = new UserDTO();
            userDTO.setPassword(tempPassword);

            log.info("{\"result\": 1, \"httpCode\": 200, \"user\": \"{}\"}", userDTO);
            return ResponseEntity.ok(new UserResponse(true,200,userDTO));
        } catch (Exception e) {
            log.error("Error during password find", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,600));
        }
    }

    @PutMapping("/users/passReset")
    public ResponseEntity<?> resetPassword(@RequestBody ChangePasswordDTO request) {
        try {
            Users user = userService.findUserByEmail(request.getEmail());

            if (user == null){
                log.info("{\"result\": 0, \"httpCode\": 404}");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false,404));
            }

            // 현재 비밀번호 검증
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                log.info("{\"result\": 0, \"httpCode\": 401, \"message\": \"Invalid current password\"}");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(false, 401, "Invalid current password"));
            }

            userService.changePassword(user.getEmail(), request.getNewPassword());
            log.info("{\"result\": 1, \"httpCode\": 200}");
            return ResponseEntity.ok(new ApiResponse(true,200));
        }catch (Exception e) {
            log.error("Error during password change", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,600));
        }
    }

    @PutMapping("/users/infoChange")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token, @RequestBody @Valid UserDTO request) {
        try {
            token = token.substring(7);

            String userEmail = jwtTokenUtil.extractUsername(token);
            Users user = userService.findUserByEmail(userEmail);
            if (user == null) {
                log.info("{\"result\": 0, \"httpCode\": 404}");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,404));
            }

            userService.update(userEmail, request.getTell(), request.getNickname());
            log.info("{\"result\": 1, \"httpCode\": 200}");
            return ResponseEntity.ok(new ApiResponse(true,200));
        } catch (Exception e) {
            log.error("Error during user update", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false,600));
        }
    }

    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token) {
        try {
            token = token.substring(7);

            String userEmail = jwtTokenUtil.extractUsername(token);
            Users user = userService.findUserByEmail(userEmail);
            if (user == null) {
                log.info("{\"result\": 0, \"httpCode\": 404}");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,404));
            }

            userService.deleteUser(user);
            log.info("{\"result\": 0, \"httpCode\": 200}");
            return ResponseEntity.ok(new ApiResponse(true,200));
        } catch (Exception e) {
            log.error("Error during user deletion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false,600));
        }
    }

    // 관리자 인증 메서드
    private boolean isAdmin(String token) {
        String role = jwtTokenUtil.extractRole(token.substring(7));
        return "ADMIN".equals(role);
    }
}