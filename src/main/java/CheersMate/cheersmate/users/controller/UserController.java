package CheersMate.cheersmate.users.controller;

import CheersMate.cheersmate.exception.CustomValidationException;
import CheersMate.cheersmate.jwt.JwtTokenUtil;
import CheersMate.cheersmate.users.dto.ApiResponse;
import CheersMate.cheersmate.users.dto.ChangePasswordDTO;
import CheersMate.cheersmate.users.dto.LoginDTO;
import CheersMate.cheersmate.users.dto.UserDTO;
import CheersMate.cheersmate.users.entity.Role;
import CheersMate.cheersmate.users.entity.Users;
import CheersMate.cheersmate.users.service.UserService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO request) {
        log.info("Received login request with email: {} and password: {}", request.getEmail(), request.getPassword());
        Users user = userService.login(request.getEmail(), request.getPassword());

        if (user != null) {
            String accessToken = jwtTokenUtil.generateAccessToken(user);
            String refreshToken = jwtTokenUtil.generateRefreshToken(user);

            log.info("{\"result\": 1, \"resultCode\": 200, \"accessToken\": \"{}\", \"refreshToken\": \"{}\"}", accessToken, refreshToken);
            return ResponseEntity.ok(new ApiResponse(1,200, accessToken, refreshToken));
        } else {
            log.info("{\"result\": 0, \"resultCode\": 600}");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(0,600));
        }
    }

    @PostMapping("/register")
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

            log.info("{\"result\": 1, \"resultCode\": 200}");
            return ResponseEntity.ok(new ApiResponse(1,200));
        } catch (CustomValidationException e) {
            log.error("Validation error during registration", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse2(0, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Error during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(0,600));
        }
    }

    @PostMapping("/emailFind")
    public ResponseEntity<?> emailFind(@RequestBody UserDTO request) {
        try {
            Users user = userService.findEmail(request.getTell(), request.getNickname());

            if (user == null) {
                log.info("{\"result\": 0, \"resultCode\": 404}");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(0,404));
            }

            log.info("{\"result\": 1, \"resultCode\": 200, \"email\": \"{}\"}", user.getEmail());
            return ResponseEntity.ok(new ApiResponse(1,200,user.getEmail()));
        } catch (Exception e) {
            log.error("Error during email find", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(0,600));
        }
    }

    @PostMapping("/passFind")
    public ResponseEntity<?> passFind(@RequestBody UserDTO request) {
        try {
            Users user = userService.findPass(request.getEmail(), request.getTell());

            if (user == null) {
                log.info("{\"result\": 0, \"resultCode\": 404}");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(0,404));
            }

            log.info("{\"result\": 1, \"resultCode\": 200, \"email\": \"{}\"}", user.getEmail());
            return ResponseEntity.ok(new ApiResponse(1,200,user.getEmail()));
        } catch (Exception e) {
            log.error("Error during password find", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(0,600));
        }
    }

    @PutMapping("/passReset")
    public ResponseEntity<?> resetPassword(@RequestBody ChangePasswordDTO request) {
        try {
            Users user = userService.findUserByEmail(request.getEmail());

            if (user == null){
                log.info("{\"result\": 0, \"resultCode\": 404}");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(0,404));
            }

            userService.changePassword(user.getEmail(), request.getPassword());
            log.info("{\"result\": 1, \"resultCode\": 200}");
            return ResponseEntity.ok(new ApiResponse(1,200));
        }catch (Exception e) {
            log.error("Error during password change", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(0,600));
        }
    }

    @PutMapping("/infoChange")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token, @RequestBody @Valid UserDTO request) {
        try {
            token = token.substring(7);

            String userEmail = jwtTokenUtil.extractUsername(token);
            Users user = userService.findUserByEmail(userEmail);
            if (user == null) {
                log.info("{\"result\": 0, \"resultCode\": 404}");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(0,404));
            }

            userService.update(userEmail, request.getTell(), request.getNickname());
            log.info("{\"result\": 1, \"resultCode\": 200}");
            return ResponseEntity.ok(new ApiResponse(1,200));
        } catch (Exception e) {
            log.error("Error during user update", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(0,600));
        }
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token) {
        try {
            token = token.substring(7);

            String userEmail = jwtTokenUtil.extractUsername(token);
            Users user = userService.findUserByEmail(userEmail);
            if (user == null) {
                log.info("{\"result\": 0, \"resultCode\": 404}");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(0,404));
            }

            userService.deleteUser(user);
            log.info("{\"result\": 0, \"resultCode\": 200}");
            return ResponseEntity.ok(new ApiResponse(1,200));
        } catch (Exception e) {
            log.error("Error during user deletion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(0,600));
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ApiResponse2 {
        private int result;
        private int resultCode;
        private String message;

        public ApiResponse2(int result, int resultCode, String message) {
            this.result = result;
            this.resultCode = resultCode;
            this.message = message;
        }
    }
}