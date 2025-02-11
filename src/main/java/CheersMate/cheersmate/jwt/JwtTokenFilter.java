package CheersMate.cheersmate.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Component
@AllArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenFilter.class);

    // permitAll URL 목록을 상수로 관리 (필요에 따라 외부화 가능)
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/users/login", "/users/register", "/users/emailFind",
            "/users/passFind", "/users/passReset", "/swagger-ui/", "/v3/api-docs",
            "/js/", "/images/", "/auth/home", "/auth/login", "/auth/refresh", "/weather", "/batch/start"
    );

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            sendErrorResponse(response, "Missing or invalid Authorization header");
            return;
        }

        String token = header.substring(7);
        try {
            if (!jwtTokenUtil.validateToken(token)) {
                log.warn("Invalid or expired token: {}", token);
                sendErrorResponse(response, "Token expired");
                return;
            }
        } catch (TokenValidationException e) {
            log.error("Token validation failed: {}", e.getMessage(), e);
            sendErrorResponse(response, e.getMessage());
            return;
        }

        String email = jwtTokenUtil.getEmail(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (userDetails != null) {
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(String.format("{\"result\": 0, \"httpCode\": %d, \"message\": \"%s\"}", HttpServletResponse.SC_UNAUTHORIZED, message));
            writer.flush();
        }
    }
}