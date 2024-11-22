package CheersMate.cheersmate.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenFilter.class);

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    public JwtTokenFilter(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/users/login") ||
                path.startsWith("/users/register") ||
                path.startsWith("/users/emailFind") ||
                path.startsWith("/users/passFind") ||
                path.startsWith("/users/passReset") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/weather") ||
                path.startsWith("/batch/start")||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.equals("/auth/home") ||
                path.equals("/auth/login")) {
            // Skip token validation for these paths
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write("{\"result\": 0, \"resultCode\": 401, \"message\": \"Missing or invalid Authorization header\"}");
            writer.flush();
            return;
        }

        String token = header.substring(7);
        try {
            if (!jwtTokenUtil.validateToken(token)) {
                log.warn("Invalid or expired token: {}", token);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                PrintWriter writer = response.getWriter();
                writer.write("{\"result\": 0, \"resultCode\": 401, \"message\": \"Token expired\"}");
                writer.flush();
                return;
            }
        } catch (TokenValidationException e) {
            log.error("Token validation failed: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write(String.format("{\"result\": 0, \"resultCode\": 401, \"message\": \"%s\"}", e.getMessage()));
            writer.flush();
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
}