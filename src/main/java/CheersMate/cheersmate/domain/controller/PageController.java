package CheersMate.cheersmate.domain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

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
