package CheersMate.cheersmate.domain.controller;

import CheersMate.cheersmate.jwt.JwtTokenUtil;
import CheersMate.cheersmate.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping("/auth/home")
    public String index() {
        return "index"; // templates/index.html을 렌더링
    }

    @GetMapping("/auth/login")
    public String login() {
        return "login";
    }

    @GetMapping("/auth/admin")
    public String adminPage() {
        return "admin"; // templates/admin.html을 렌더링
    }
}
