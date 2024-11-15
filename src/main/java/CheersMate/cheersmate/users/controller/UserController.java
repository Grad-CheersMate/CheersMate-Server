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
import CheersMate.cheersmate.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserController {
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

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

    @PostMapping("/users/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO request) {
        log.info("Received login request with email: {} and password: {}", request.getEmail(), request.getPassword());
        Users user = userService.login(request.getEmail(), request.getPassword());

        if (user != null) {
            String accessToken = jwtTokenUtil.generateAccessToken(user);
            String refreshToken = jwtTokenUtil.generateRefreshToken(user);

            log.info("{\"result\": 1, \"httpCode\": 200, \"accessToken\": \"{}\", \"refreshToken\": \"{}\"}", accessToken, refreshToken);
            return ResponseEntity.ok(new ApiResponse(true,200, accessToken, refreshToken));
        } else {
            log.info("{\"result\": 0, \"httpCode\": 600}");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,600));
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